package com.orbit.spatialjournal.domain.usecase

import com.orbit.spatialjournal.domain.repository.VisitRepository
import com.orbit.spatialjournal.location.LocationSample
import com.orbit.spatialjournal.location.VisitDetectionEngine
import javax.inject.Inject

/** Drives VisitDetectionEngine over a batch of raw samples and persists SUGGESTED candidates. */
class RunVisitDetectionUseCase @Inject constructor(
    private val engine: VisitDetectionEngine,
    private val visitRepository: VisitRepository
) {
    suspend operator fun invoke(samples: List<LocationSample>) {
        val candidates = engine.detectVisits(samples)
        candidates.forEach { visitRepository.saveCandidate(it) }
    }
}
