package com.orbit.spatialjournal.domain.usecase

import com.orbit.spatialjournal.core.model.Memory
import com.orbit.spatialjournal.core.model.Trip
import com.orbit.spatialjournal.core.model.TripStatus
import com.orbit.spatialjournal.core.util.GeoMath
import com.orbit.spatialjournal.core.util.IdGenerator
import com.orbit.spatialjournal.domain.repository.MemoryRepository
import com.orbit.spatialjournal.domain.repository.TripRepository
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * The Trip Detection / Trip Builder engine. Two entry points:
 *  - [createManualTrip]: user explicitly groups a set of memories into a named Trip.
 *  - [suggestTrips]: scans unassigned memories for city changes across a minimum distance
 *    and gap, and proposes candidate Trips the user can accept or dismiss (never auto-applied).
 */
class BuildTripUseCase @Inject constructor(
    private val memoryRepository: MemoryRepository,
    private val tripRepository: TripRepository
) {
    suspend fun createManualTrip(name: String, memoryIds: List<String>, tags: List<String> = emptyList()): Trip {
        val memories = memoryIds.mapNotNull { memoryRepository.getMemory(it) }
        val sorted = memories.sortedBy { it.timestamp }
        val trip = Trip(
            id = IdGenerator.newId(), name = name,
            startDate = sorted.firstOrNull()?.timestamp ?: System.currentTimeMillis(),
            endDate = sorted.lastOrNull()?.timestamp, status = TripStatus.COMPLETED,
            memoryIds = memoryIds, tags = tags, isAutoSuggested = false
        )
        tripRepository.saveTrip(trip)
        memories.forEach { memoryRepository.saveMemory(it.copy(tripId = trip.id)) }
        return trip
    }

    /** Minimum straight-line jump between consecutive memories (by time) to call it a "trip". */
    private val MIN_TRIP_DISTANCE_METERS = 80_000.0 // ~80km, i.e. a different city/region
    private val MAX_GAP_BETWEEN_MEMORIES_HOURS = 72L

    suspend fun suggestTrips(unassignedMemories: List<Memory>): List<Trip> {
        val withLocation = unassignedMemories.filter { it.location != null && it.tripId == null }
            .sortedBy { it.timestamp }
        if (withLocation.size < 2) return emptyList()

        val suggestions = mutableListOf<Trip>()
        var groupStart = 0
        for (i in 1 until withLocation.size) {
            val prev = withLocation[i - 1]
            val curr = withLocation[i]
            val gapHours = TimeUnit.MILLISECONDS.toHours(curr.timestamp - prev.timestamp)
            val distance = GeoMath.distanceMeters(prev.location!!, curr.location!!)

            val breaksGroup = gapHours > MAX_GAP_BETWEEN_MEMORIES_HOURS && distance > MIN_TRIP_DISTANCE_METERS
            if (breaksGroup) {
                emitSuggestionIfMeaningful(withLocation.subList(groupStart, i))?.let(suggestions::add)
                groupStart = i
            }
        }
        emitSuggestionIfMeaningful(withLocation.subList(groupStart, withLocation.size))?.let(suggestions::add)
        return suggestions
    }

    private fun emitSuggestionIfMeaningful(group: List<Memory>): Trip? {
        if (group.size < 2) return null
        val cities = group.mapNotNull { it.city }.distinct()
        if (cities.size < 2 && group.mapNotNull { it.country }.distinct().size < 2) return null

        return Trip(
            id = IdGenerator.newId(),
            name = cities.take(2).joinToString(" → ").ifBlank { "Suggested trip" },
            startDate = group.first().timestamp, endDate = group.last().timestamp,
            status = TripStatus.SUGGESTED, memoryIds = group.map { it.id }, isAutoSuggested = true
        )
    }
}
