package com.orbit.spatialjournal.location

import com.orbit.spatialjournal.core.model.GeoPoint

data class LocationSample(
    val point: GeoPoint,
    val timestamp: Long,
    val speedMps: Float? = null
)

enum class LocationPermissionState { GRANTED_FINE, GRANTED_COARSE, DENIED }
