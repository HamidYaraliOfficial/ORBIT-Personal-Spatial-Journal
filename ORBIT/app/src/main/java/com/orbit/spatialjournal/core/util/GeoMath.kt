package com.orbit.spatialjournal.core.util

import com.orbit.spatialjournal.core.model.GeoPoint
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/** Pure geographic math shared by the Visit Detection Engine, Trip Builder and Map layer. */
object GeoMath {

    private const val EARTH_RADIUS_METERS = 6_371_000.0

    /** Great-circle distance between two points, in meters. */
    fun distanceMeters(a: GeoPoint, b: GeoPoint): Double {
        val lat1 = Math.toRadians(a.latitude)
        val lat2 = Math.toRadians(b.latitude)
        val dLat = Math.toRadians(b.latitude - a.latitude)
        val dLon = Math.toRadians(b.longitude - a.longitude)

        val h = sin(dLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(h), sqrt(1 - h))
        return EARTH_RADIUS_METERS * c
    }

    /** Centroid of a cluster of points; used by the Visit Detection Engine and Map clustering fallback. */
    fun centroid(points: List<GeoPoint>): GeoPoint {
        require(points.isNotEmpty()) { "Cannot compute centroid of an empty point list" }
        val lat = points.sumOf { it.latitude } / points.size
        val lon = points.sumOf { it.longitude } / points.size
        return GeoPoint(lat, lon)
    }

    /** Simple bounding-box expansion used when deciding how far to zoom the map to fit a set of points. */
    fun boundingBox(points: List<GeoPoint>): Pair<GeoPoint, GeoPoint> {
        val minLat = points.minOf { it.latitude }
        val maxLat = points.maxOf { it.latitude }
        val minLon = points.minOf { it.longitude }
        val maxLon = points.maxOf { it.longitude }
        return GeoPoint(minLat, minLon) to GeoPoint(maxLat, maxLon)
    }
}
