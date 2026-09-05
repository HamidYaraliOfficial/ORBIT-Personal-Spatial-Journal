package com.orbit.spatialjournal.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.orbit.spatialjournal.media.MediaImporter
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class MediaImportWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val mediaImporter: MediaImporter
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val uris = inputData.getStringArray(KEY_URIS)?.toList() ?: return Result.failure()
        return try {
            mediaImporter.importUris(uris)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val KEY_URIS = "uris"
    }
}
