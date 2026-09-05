package com.orbit.spatialjournal.map

import com.orbit.spatialjournal.core.model.GeoPoint
import com.orbit.spatialjournal.core.model.Memory
import com.orbit.spatialjournal.core.util.Constants
import com.orbit.spatialjournal.core.util.GeoMath

data class MapCluster(
    val center: GeoPoint,
    val memories: List<Memory>
) {
    val size: Int get() = memories.size
}

/**
 * Grid-based clustering used to keep the Map Screen smooth with large memory counts.
 * Cell size shrinks as zoom increases, which is what makes marker density feel adaptive
 * (many pins group into one cluster bubble when zoomed out; individual pins appear once
 * zoom passes MAP_CLUSTER_MIN_ZOOM_FOR_INDIVIDUAL_MARKERS).
 */
object MarkerClusterManager {

    fun cluster(memories: List<Memory>, zoomLevel: Float): List<MapCluster> {
        val located = memories.filter { it.location != null }
        if (zoomLevel >= Constants.MAP_CLUSTER_MIN_ZOOM_FOR_INDIVIDUAL_MARKERS) {
            return located.map { MapCluster(it.location!!, listOf(it)) }
        }

        val cellSizeDegrees = cellSizeForZoom(zoomLevel)
        val buckets = LinkedHashMap<Pair<Int, Int>, MutableList<Memory>>()
        for (memory in located) {
            val loc = memory.location!!
            val key = (loc.latitude / cellSizeDegrees).toInt() to (loc.longitude / cellSizeDegrees).toInt()
            buckets.getOrPut(key) { mutableListOf() }.add(memory)
        }

        return buckets.values.map { group ->
            MapCluster(center = GeoMath.centroid(group.map { it.location!! }), memories = group)
        }
    }

    private fun cellSizeForZoom(zoom: Float): Double = when {
        zoom < 3f -> 20.0
        zoom < 6f -> 8.0
        zoom < 9f -> 2.0
        zoom < 12f -> 0.5
        else -> 0.08
    }
}
