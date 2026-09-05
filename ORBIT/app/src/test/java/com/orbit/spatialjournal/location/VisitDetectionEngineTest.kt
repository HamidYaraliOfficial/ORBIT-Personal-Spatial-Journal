package com.orbit.spatialjournal.location

import com.google.common.truth.Truth.assertThat
import com.orbit.spatialjournal.core.model.GeoPoint
import com.orbit.spatialjournal.core.model.VisitStatus
import org.junit.Test

class VisitDetectionEngineTest {

    private val engine = VisitDetectionEngine()
    private val basePoint = GeoPoint(35.6892, 51.3890)
    private val baseTime = 1_700_000_000_000L

    private fun sampleAt(minutesOffset: Long, jitterMeters: Double = 0.0) = LocationSample(
        point = GeoPoint(basePoint.latitude + jitterMeters * 0.000009, basePoint.longitude),
        timestamp = baseTime + minutesOffset * 60_000
    )

    @Test
    fun `dense long dwell cluster produces a suggested visit`() {
        val samples = listOf(sampleAt(0), sampleAt(4), sampleAt(8), sampleAt(12), sampleAt(16), sampleAt(20))
        val visits = engine.detectVisits(samples, minDwellMinutes = 15, minSupportingPoints = 3)
        assertThat(visits).hasSize(1)
        assertThat(visits.first().status).isEqualTo(VisitStatus.SUGGESTED)
        assertThat(visits.first().confidence).isLessThan(1.0f)
    }

    @Test
    fun `single passing point never becomes a visit`() {
        val samples = listOf(sampleAt(0))
        val visits = engine.detectVisits(samples)
        assertThat(visits).isEmpty()
    }

    @Test
    fun `short dwell below threshold is rejected`() {
        val samples = listOf(sampleAt(0), sampleAt(1), sampleAt(2))
        val visits = engine.detectVisits(samples, minDwellMinutes = 30, minSupportingPoints = 2)
        assertThat(visits).isEmpty()
    }

    @Test
    fun `two distant clusters produce two visits`() {
        val farPoint = GeoPoint(40.0, 60.0)
        val samples = listOf(
            sampleAt(0), sampleAt(5), sampleAt(10), sampleAt(15),
            LocationSample(farPoint, baseTime + 60 * 60_000),
            LocationSample(farPoint, baseTime + 65 * 60_000),
            LocationSample(farPoint, baseTime + 80 * 60_000)
        )
        val visits = engine.detectVisits(samples, minDwellMinutes = 10, minSupportingPoints = 3)
        assertThat(visits).hasSize(2)
    }

    @Test
    fun `empty sample list returns no visits`() {
        assertThat(engine.detectVisits(emptyList())).isEmpty()
    }
}
