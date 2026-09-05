package com.orbit.spatialjournal.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.orbit.spatialjournal.domain.usecase.RunVisitDetectionUseCase
import com.orbit.spatialjournal.location.LocationTrackingService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withTimeoutOrNull

/** Periodically drains recently-sampled location points into the Visit Detection Engine. */
@HiltWorker
class VisitDetectionWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val runVisitDetection: RunVisitDetectionUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val samples = withTimeoutOrNull(5_000) {
                LocationTrackingService.recentSamples.toList()
            }.orEmpty()
            if (samples.isNotEmpty()) runVisitDetection(samples)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
