package com.orbit.spatialjournal.location

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.orbit.spatialjournal.notifications.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Fires on geofence ENTER/EXIT for Spatial Notes and Place Reminders. Kept intentionally
 * dumb: it only decides whether to show a local notification, never performs network calls,
 * so it stays fast and reliable inside the OS's tight broadcast-receiver time budget.
 */
@AndroidEntryPoint
class GeofenceBroadcastReceiver : BroadcastReceiver() {

    @Inject lateinit var notificationHelper: NotificationHelper

    override fun onReceive(context: Context, intent: Intent) {
        val event = GeofencingEvent.fromIntent(intent) ?: return
        if (event.hasError()) {
            val error = GeofenceStatusCodes.getStatusCodeString(event.errorCode)
            android.util.Log.w("GeofenceReceiver", "Geofence error: $error")
            return
        }
        if (event.geofenceTransition != Geofence.GEOFENCE_TRANSITION_ENTER) return

        val triggeringIds = event.triggeringGeofences?.map { it.requestId } ?: return
        triggeringIds.forEach { id ->
            notificationHelper.showPlaceReminderNotification(reminderId = id)
        }
    }
}
