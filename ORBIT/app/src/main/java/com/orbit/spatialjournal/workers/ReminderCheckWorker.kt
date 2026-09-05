package com.orbit.spatialjournal.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.orbit.spatialjournal.domain.repository.ReminderRepository
import com.orbit.spatialjournal.location.GeofenceManager
import com.orbit.spatialjournal.location.GeofenceSpec
import com.orbit.spatialjournal.notifications.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/** Fires any due time-based reminders and (re)registers geofences for location-based ones. */
@HiltWorker
class ReminderCheckWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val reminderRepository: ReminderRepository,
    private val geofenceManager: GeofenceManager,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val due = reminderRepository.getDueTimeReminders(System.currentTimeMillis())
            due.forEach { reminder ->
                notificationHelper.showReminderNotification(reminder.id, reminder.title)
                reminderRepository.markFired(reminder.id)
            }

            val geofenceReminders = reminderRepository.getPendingGeofenceReminders()
            val specs = geofenceReminders.mapNotNull { r ->
                if (r.geofenceLatitude != null && r.geofenceLongitude != null) {
                    GeofenceSpec(r.id, r.geofenceLatitude, r.geofenceLongitude, r.geofenceRadiusMeters ?: 150f)
                } else null
            }
            geofenceManager.registerAll(specs)

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
