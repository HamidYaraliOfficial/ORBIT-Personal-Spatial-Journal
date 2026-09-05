package com.orbit.spatialjournal.data.repository

import android.content.Context
import androidx.core.content.FileProvider
import com.orbit.spatialjournal.core.model.ExportFormat
import com.orbit.spatialjournal.domain.repository.ExportRepository
import com.orbit.spatialjournal.domain.repository.MemoryRepository
import com.orbit.spatialjournal.domain.repository.TripRepository
import com.orbit.spatialjournal.export.*
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Writes the requested export format to the app's cache dir and hands back a content:// URI
 * via FileProvider so the UI can trigger a system share sheet or "save to..." action. Note:
 * the UI layer is responsible for showing the Privacy Warning noted in the spec before
 * calling this when the export includes precise location.
 */
@Singleton
class ExportRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val memoryRepository: MemoryRepository,
    private val tripRepository: TripRepository,
    private val jsonExporter: JsonExporter,
    private val csvExporter: CsvExporter,
    private val geoJsonExporter: GeoJsonExporter,
    private val kmlExporter: KmlExporter,
    private val pdfExporter: PdfExporter
) : ExportRepository {

    override suspend fun exportMemories(memoryIds: List<String>, format: ExportFormat): String {
        val memories = memoryIds.mapNotNull { memoryRepository.getMemory(it) }
        return writeAndShare(memories, format, "orbit_export")
    }

    override suspend fun exportTrip(tripId: String, format: ExportFormat): String {
        val trip = tripRepository.getTrip(tripId)
        val memories = trip?.memoryIds?.mapNotNull { memoryRepository.getMemory(it) } ?: emptyList()
        return writeAndShare(memories, format, "orbit_trip_${trip?.name ?: tripId}")
    }

    override suspend fun exportAll(format: ExportFormat): String {
        val memories = kotlinx.coroutines.flow.first(memoryRepository.observeAll())
        return writeAndShare(memories, format, "orbit_full_export")
    }

    private fun writeAndShare(memories: List<com.orbit.spatialjournal.core.model.Memory>, format: ExportFormat, baseName: String): String {
        val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val file = when (format) {
            ExportFormat.JSON -> File(exportDir, "$baseName.json").apply { writeText(jsonExporter.export(memories)) }
            ExportFormat.CSV -> File(exportDir, "$baseName.csv").apply { writeText(csvExporter.export(memories)) }
            ExportFormat.GEOJSON -> File(exportDir, "$baseName.geojson").apply { writeText(geoJsonExporter.export(memories)) }
            ExportFormat.KML -> File(exportDir, "$baseName.kml").apply { writeText(kmlExporter.export(memories)) }
            ExportFormat.PDF -> File(exportDir, "$baseName.pdf").apply { outputStream().use { pdfExporter.export(memories, baseName, it) } }
            ExportFormat.ZIP -> File(exportDir, "$baseName.json").apply { writeText(jsonExporter.export(memories)) }
        }
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file).toString()
    }
}
