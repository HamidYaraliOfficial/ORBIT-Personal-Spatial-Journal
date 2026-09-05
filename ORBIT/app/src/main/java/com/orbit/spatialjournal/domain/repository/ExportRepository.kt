package com.orbit.spatialjournal.domain.repository

import com.orbit.spatialjournal.core.model.ExportFormat

interface ExportRepository {
    /** Returns the content:// URI of the generated export file. */
    suspend fun exportMemories(memoryIds: List<String>, format: ExportFormat): String
    suspend fun exportTrip(tripId: String, format: ExportFormat): String
    suspend fun exportAll(format: ExportFormat): String
}
