package com.orbit.spatialjournal.media

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import com.orbit.spatialjournal.core.model.*
import com.orbit.spatialjournal.core.util.HashUtils
import com.orbit.spatialjournal.core.util.IdGenerator
import com.orbit.spatialjournal.domain.repository.ImportProgress
import com.orbit.spatialjournal.domain.repository.MemoryRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Batch-imports existing photos/videos from the device into ORBIT as Memories, preserving
 * EXIF metadata wherever present. Supports pause/resume/retry so a large gallery import
 * doesn't have to restart from zero if the user backgrounds the app (see ImportWorker,
 * which drives this on a WorkManager thread).
 */
@Singleton
class MediaImporter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val memoryRepository: MemoryRepository,
    private val exifExtractor: ExifExtractor
) {
    private val _progress = MutableStateFlow(ImportProgress(0, 0, ""))
    val progress: StateFlow<ImportProgress> = _progress.asStateFlow()

    @Volatile private var paused = false
    @Volatile private var cancelled = false
    private var failedUris = mutableListOf<String>()

    fun pause() { paused = true }
    fun resume() { paused = false }
    fun cancel() { cancelled = true }

    suspend fun importUris(uris: List<String>) {
        cancelled = false
        var processed = 0
        var failed = 0
        _progress.value = ImportProgress(uris.size, 0, "", isPaused = false)

        for (uriString in uris) {
            if (cancelled) break
            while (paused) kotlinx.coroutines.delay(400)

            val label = uriString.substringAfterLast('/')
            _progress.value = _progress.value.copy(currentItemLabel = label)

            val success = runCatching { importOne(uriString) }.isSuccess
            if (!success) { failed++; failedUris.add(uriString) }

            processed++
            _progress.value = _progress.value.copy(processedItems = processed, failedItems = failed)
        }
        _progress.value = _progress.value.copy(isComplete = true)
    }

    suspend fun retryFailed() {
        val toRetry = failedUris.toList()
        failedUris.clear()
        importUris(toRetry)
    }

    private suspend fun importOne(uriString: String) {
        val uri = Uri.parse(uriString)
        val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"
        val exif = if (mimeType.startsWith("image/")) exifExtractor.extract(uriString) else null

        val sha256 = runCatching {
            context.contentResolver.openInputStream(uri)?.use { HashUtils.sha256(it.readBytes()) }
        }.getOrNull()

        val perceptualHash = if (mimeType.startsWith("image/")) {
            runCatching {
                context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
                    ?.let { HashUtils.averageHash(it) }
            }.getOrNull()
        } else null

        val timestamp = exif?.capturedAt ?: System.currentTimeMillis()
        val type = when {
            mimeType.startsWith("image/") -> MemoryType.PHOTO
            mimeType.startsWith("video/") -> MemoryType.VIDEO
            mimeType.startsWith("audio/") -> MemoryType.VOICE
            else -> MemoryType.DOCUMENT
        }

        val memory = Memory(
            id = IdGenerator.newId(),
            title = uriString.substringAfterLast('/'),
            type = type,
            timestamp = timestamp,
            location = exif?.latitude?.let { lat -> exif.longitude?.let { lon -> GeoPoint(lat, lon) } },
            attachments = listOf(
                Attachment(
                    id = IdGenerator.newId(), uri = uriString, mimeType = mimeType,
                    exif = exif
                )
            ),
            source = MemorySource.IMPORTED_GALLERY,
            metadata = buildMap {
                sha256?.let { put("sha256", it) }
                perceptualHash?.let { put("perceptualHash", it.toString()) }
            }
        )
        memoryRepository.saveMemory(memory)
    }
}
