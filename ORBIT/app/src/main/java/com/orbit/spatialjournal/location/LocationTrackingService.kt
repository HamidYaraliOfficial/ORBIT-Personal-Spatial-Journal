package com.orbit.spatialjournal.location

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.orbit.spatialjournal.MainActivity
import com.orbit.spatialjournal.R
import com.orbit.spatialjournal.core.model.GeoPoint
import com.orbit.spatialjournal.core.model.LocationMode
import com.orbit.spatialjournal.core.util.Constants
import com.orbit.spatialjournal.data.datastore.SettingsDataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Foreground service that only runs while the user has explicitly chosen the
 * SMART_CONTEXT or BACKGROUND_REMINDERS location mode (see Privacy Center). It samples
 * location at the interval BatteryAwareLocationScheduler recommends and forwards raw
 * points to VisitDetectionEngine via WorkManager rather than doing heavy work inline.
 *
 * A persistent, low-priority notification is always shown while this service runs — ORBIT
 * never tracks location silently in the background.
 */
@AndroidEntryPoint
class LocationTrackingService : Service() {

    @Inject lateinit var settingsDataStore: SettingsDataStore
    @Inject lateinit var scheduler: BatteryAwareLocationScheduler

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var fusedClient: FusedLocationProviderClient
    private var callback: LocationCallback? = null

    companion object {
        const val NOTIFICATION_ID = 4201
        val recentSamples = MutableSharedFlow<LocationSample>(replay = 0, extraBufferCapacity = 64)
    }

    override fun onCreate() {
        super.onCreate()
        fusedClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, buildNotification())
        serviceScope.launch {
            val mode = settingsDataStore.locationMode.first()
            if (mode == LocationMode.OFF || mode == LocationMode.MANUAL || mode == LocationMode.WHILE_USING) {
                stopSelf()
                return@launch
            }
            startSampling()
        }
        return START_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun startSampling() {
        val interval = scheduler.currentIntervalMillis()
        val request = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, interval)
            .setMinUpdateIntervalMillis(Constants.LOCATION_FASTEST_INTERVAL_MS)
            .build()

        callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                serviceScope.launch {
                    recentSamples.emit(
                        LocationSample(
                            point = GeoPoint(location.latitude, location.longitude, location.accuracy),
                            timestamp = location.time
                        )
                    )
                }
            }
        }
        fusedClient.requestLocationUpdates(request, callback as LocationCallback, mainLooper)
    }

    private fun buildNotification(): Notification {
        val openAppIntent = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_VISITS)
            .setContentTitle(getString(R.string.location_service_notification_title))
            .setContentText(getString(R.string.location_service_notification_body))
            .setSmallIcon(R.drawable.ic_orbit_marker)
            .setContentIntent(openAppIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()
    }

    override fun onDestroy() {
        callback?.let { fusedClient.removeLocationUpdates(it) }
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
