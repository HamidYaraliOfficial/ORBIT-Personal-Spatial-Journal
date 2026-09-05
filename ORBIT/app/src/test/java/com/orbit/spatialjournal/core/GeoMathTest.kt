package com.orbit.spatialjournal.core

import com.google.common.truth.Truth.assertThat
import com.orbit.spatialjournal.core.model.GeoPoint
import com.orbit.spatialjournal.core.util.GeoMath
import org.junit.Test

class GeoMathTest {

    @Test
    fun `distance between identical points is zero`() {
        val p = GeoPoint(35.6892, 51.3890)
        assertThat(GeoMath.distanceMeters(p, p)).isWithin(0.001).of(0.0)
    }

    @Test
    fun `distance between Tehran and Isfahan is roughly correct`() {
        val tehran = GeoPoint(35.6892, 51.3890)
        val isfahan = GeoPoint(32.6546, 51.6680)
        val distanceKm = GeoMath.distanceMeters(tehran, isfahan) / 1000.0
        // Great-circle distance is ~340km; allow generous tolerance for a straight-line check.
        assertThat(distanceKm).isIn(300.0..380.0)
    }

    @Test
    fun `centroid of a single point equals that point`() {
        val p = GeoPoint(10.0, 20.0)
        val centroid = GeoMath.centroid(listOf(p))
        assertThat(centroid.latitude).isEqualTo(10.0)
        assertThat(centroid.longitude).isEqualTo(20.0)
    }

    @Test
    fun `centroid averages multiple points`() {
        val points = listOf(GeoPoint(0.0, 0.0), GeoPoint(10.0, 10.0))
        val centroid = GeoMath.centroid(points)
        assertThat(centroid.latitude).isEqualTo(5.0)
        assertThat(centroid.longitude).isEqualTo(5.0)
    }
}
