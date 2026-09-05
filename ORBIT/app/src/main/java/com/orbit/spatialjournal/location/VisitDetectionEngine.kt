package com.orbit.spatialjournal.location

import com.orbit.spatialjournal.core.model.GeoPoint
import com.orbit.spatialjournal.core.model.VisitCandidate
import com.orbit.spatialjournal.core.model.VisitStatus
import com.orbit.spatialjournal.core.util.Constants
import com.orbit.spatialjournal.core.util.GeoMath
import com.orbit.spatialjournal.core.util.IdGenerator
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Pure, unit-testable clustering logic behind the "Visit Detection Engine" described in the
 * spec: it never claims certainty. Every result is a [VisitCandidate] with status SUGGESTED
 * and a confidence score — the user always confirms, edits, or rejects it (see VisitRepository).
 *
 * Algorithm: a simple single-pass spatial/temporal clustering over a chronologically-sorted
 * list of raw location samples. A cluster becomes a candidate visit once it has both enough
 * supporting points AND a long enough dwell time — a single passing GPS ping never counts.
 */
class VisitDetectionEngine @Inject constructor() {

    fun detectVisits(
        samples: List<LocationSample>,
        clusterRadiusMeters: Double = Constants.VISIT_CLUSTER_RADIUS_METERS,
        minDwellMinutes: Int = Constants.VISIT_MIN_DWELL_MINUTES,
        minSupportingPoints: Int = Constants.VISIT_MIN_SUPPORTING_POINTS
    ): List<VisitCandidate> {
        if (samples.isEmpty()) return emptyList()
        val sorted = samples.sortedBy { it.timestamp }
        val candidates = mutableListOf<VisitCandidate>()
        var clusterStart = 0

        var i = 1
        while (i <= sorted.size) {
            val stillInCluster = i < sorted.size &&
                GeoMath.distanceMeters(sorted[clusterStart].point, sorted[i].point) <= clusterRadiusMeters
            if (!stillInCluster) {
                val cluster = sorted.subList(clusterStart, i)
                maybeEmitCandidate(cluster, minDwellMinutes, minSupportingPoints)?.let(candidates::add)
                clusterStart = i
            }
            i++
        }
        return candidates
    }

    private fun maybeEmitCandidate(
        cluster: List<LocationSample>,
        minDwellMinutes: Int,
        minSupportingPoints: Int
    ): VisitCandidate? {
        if (cluster.size < minSupportingPoints) return null
        val dwellMinutes = TimeUnit.MILLISECONDS.toMinutes(cluster.last().timestamp - cluster.first().timestamp)
        if (dwellMinutes < minDwellMinutes) return null

        val centroid: GeoPoint = GeoMath.centroid(cluster.map { it.point })
        // Confidence rises with more supporting points and longer dwell time, capped at 0.97
        // so the UI never implies total certainty (see Place Detail "Suggested Visit" copy).
        val pointScore = (cluster.size.toFloat() / (minSupportingPoints * 4)).coerceAtMost(0.5f)
        val dwellScore = (dwellMinutes.toFloat() / (minDwellMinutes * 6)).coerceAtMost(0.47f)
        val confidence = (0.5f + pointScore + dwellScore).coerceAtMost(0.97f)

        return VisitCandidate(
            id = IdGenerator.newId(),
            location = centroid,
            placeName = null,
            arrivalAt = cluster.first().timestamp,
            departureAt = cluster.last().timestamp,
            confidence = confidence,
            status = VisitStatus.SUGGESTED,
            supportingPointCount = cluster.size
        )
    }
}
