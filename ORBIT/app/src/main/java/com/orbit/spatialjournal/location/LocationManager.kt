package com.orbit.spatialjournal.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.orbit.spatialjournal.core.model.GeoPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Thin, testable wrapper around FusedLocationProviderClient. Every call here is opt-in —
 * ORBIT never asks for a location unless a specific feature (Smart Capture, Visit Detection
 * running in an allowed LocationMode, a manual "use current location" tap) explicitly needs one.
 */
@Singleton
class OrbitLocationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fusedClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    fun permissionState(): LocationPermissionState = when {
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ->
            LocationPermissionState.GRANTED_FINE
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ->
            LocationPermissionState.GRANTED_COARSE
        else -> LocationPermissionState.DENIED
    }

    fun hasBackgroundPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocationOnce(): LocationSample? {
        if (permissionState() == LocationPermissionState.DENIED) return null
        val request = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setDurationMillis(10_000)
            .build()
        val location = fusedClient.getCurrentLocation(request, null).await() ?: return null
        return LocationSample(
            point = GeoPoint(location.latitude, location.longitude, location.accuracy),
            timestamp = location.time,
            speedMps = if (location.hasSpeed()) location.speed else null
        )
    }
}
