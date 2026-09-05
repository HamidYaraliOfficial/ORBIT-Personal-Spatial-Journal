package com.orbit.spatialjournal.domain.usecase

import com.orbit.spatialjournal.core.model.OpeningStatus
import com.orbit.spatialjournal.core.util.OpeningHoursUtils
import com.orbit.spatialjournal.domain.repository.PlaceRepository
import java.time.LocalDateTime
import javax.inject.Inject

/** Computes "open now / closes in X" or "closed / opens in X" for a Place's user-entered hours. */
class ComputeOpeningStatusUseCase @Inject constructor(
    private val placeRepository: PlaceRepository
) {
    suspend operator fun invoke(placeId: String, now: LocalDateTime = LocalDateTime.now()): OpeningStatus? {
        val place = placeRepository.getPlace(placeId) ?: return null
        if (place.openingHours.isEmpty()) return null
        return OpeningHoursUtils.computeStatus(place.openingHours, now)
    }
}
