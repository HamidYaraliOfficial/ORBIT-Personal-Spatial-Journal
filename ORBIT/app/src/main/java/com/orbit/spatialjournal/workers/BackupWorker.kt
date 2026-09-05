package com.orbit.spatialjournal.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.orbit.spatialjournal.domain.repository.BackupRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/** Runs a scheduled local backup (see BackupScreen for the user-configurable cadence). */
@HiltWorker
class BackupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val backupRepository: BackupRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val destinationUri = inputData.getString(KEY_DESTINATION_URI) ?: return Result.failure()
        val encrypt = inputData.getBoolean(KEY_ENCRYPT, true)
        return try {
            backupRepository.createBackup(destinationUri, encrypt, passphrase = null)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val KEY_DESTINATION_URI = "destination_uri"
        const val KEY_ENCRYPT = "encrypt"
    }
}
