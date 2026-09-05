package com.orbit.spatialjournal.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.orbit.spatialjournal.domain.usecase.DetectDuplicatesUseCase
import com.orbit.spatialjournal.notifications.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DuplicateScanWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val detectDuplicates: DetectDuplicatesUseCase,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val duplicates = detectDuplicates()
            if (duplicates.isNotEmpty()) {
                notificationHelper.showDuplicatesFoundNotification(duplicates.size)
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
