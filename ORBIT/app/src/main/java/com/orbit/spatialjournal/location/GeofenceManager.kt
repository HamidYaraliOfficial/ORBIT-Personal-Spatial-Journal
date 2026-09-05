package com.orbit.spatialjournal.location

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.orbit.spatialjournal.core.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

data class GeofenceSpec(val id: String, val latitude: Double, val longitude: Double, val radiusMeters: Float)

/**
 * Registers/unregisters Android geofences for Spatial Notes and Place Reminders. Deliberately
 * capped below the OS's 100-geofence limit (see Constants.MAX_ACTIVE_GEOFENCES) and always
 * has an expiration so a stale geofence can't silently keep sampling the radio forever.
 */
@Singleton
class GeofenceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val client: GeofencingClient by lazy { LocationServices.getGeofencingClient(context) }

    private val pendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    @SuppressLint("MissingPermission")
    suspend fun registerAll(specs: List<GeofenceSpec>) {
        if (specs.isEmpty()) return
        val capped = specs.take(Constants.MAX_ACTIVE_GEOFENCES)
        val geofences = capped.map { spec ->
            Geofence.Builder()
                .setRequestId(spec.id)
                .setCircularRegion(spec.latitude, spec.longitude, spec.radiusMeters)
                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_MS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                .build()
        }
        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(geofences)
            .build()
        runCatching { client.addGeofences(request, pendingIntent).await() }
    }

    suspend fun unregisterAll() {
        runCatching { client.removeGeofences(pendingIntent).await() }
    }
}
