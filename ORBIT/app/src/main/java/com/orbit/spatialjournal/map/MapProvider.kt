package com.orbit.spatialjournal.map

import com.orbit.spatialjournal.core.model.GeoPoint
import com.orbit.spatialjournal.core.model.MapStyleOption

/**
 * Abstraction over the underlying map SDK. ORBIT ships a Google Maps Compose implementation
 * ([GoogleMapProviderImpl]) by default, but every screen talks to this interface so a future
 * MapLibre/offline-vector-tile backend can be swapped in without touching UI code, per the
 * "modular map layer" requirement.
 */
interface MapProvider {
    val name: String
    fun supportsOfflineTiles(): Boolean
    fun styleResourceFor(option: MapStyleOption): Int?
}

class GoogleMapProviderImpl @javax.inject.Inject constructor() : MapProvider {
    override val name: String = "google_maps"
    override fun supportsOfflineTiles(): Boolean = false // Google Maps SDK manages its own tile cache internally.
    override fun styleResourceFor(option: MapStyleOption): Int? = when (option) {
        MapStyleOption.STANDARD -> null // default style
        MapStyleOption.DARK -> com.orbit.spatialjournal.R.raw.map_style_dark
        MapStyleOption.MINIMAL -> com.orbit.spatialjournal.R.raw.map_style_minimal
        MapStyleOption.TRAVEL -> com.orbit.spatialjournal.R.raw.map_style_travel
    }
}
