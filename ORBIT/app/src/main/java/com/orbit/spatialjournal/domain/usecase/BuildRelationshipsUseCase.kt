package com.orbit.spatialjournal.domain.usecase

import com.orbit.spatialjournal.core.model.Memory
import com.orbit.spatialjournal.core.model.MemoryRelationship
import com.orbit.spatialjournal.core.model.RelationshipType
import com.orbit.spatialjournal.core.util.DateTimeUtils
import com.orbit.spatialjournal.core.util.GeoMath
import com.orbit.spatialjournal.domain.repository.RelationshipRepository
import javax.inject.Inject

/**
 * The Memory Relationship Engine: derives SAME_PLACE / SAME_DAY / SAME_TRIP / NEARBY /
 * RELATED_TOPIC edges between a new memory and its existing neighbors so the Personal
 * Graph View and "Related Memories" panel have real data to show.
 */
class BuildRelationshipsUseCase @Inject constructor(
    private val relationshipRepository: RelationshipRepository
) {
    suspend operator fun invoke(target: Memory, candidates: List<Memory>) {
        val relationships = mutableListOf<MemoryRelationship>()
        for (candidate in candidates) {
            if (candidate.id == target.id) continue

            if (target.tripId != null && target.tripId == candidate.tripId) {
                relationships += MemoryRelationship(target.id, candidate.id, RelationshipType.SAME_TRIP)
            }
            if (target.placeName != null && target.placeName == candidate.placeName) {
                relationships += MemoryRelationship(target.id, candidate.id, RelationshipType.SAME_PLACE)
            }
            if (DateTimeUtils.isSameCalendarDay(target.timestamp, candidate.timestamp)) {
                relationships += MemoryRelationship(target.id, candidate.id, RelationshipType.SAME_DAY)
            }
            val sharedTags = target.tags.toSet().intersect(candidate.tags.toSet())
            if (sharedTags.isNotEmpty()) {
                relationships += MemoryRelationship(target.id, candidate.id, RelationshipType.RELATED_TOPIC, sharedTags.size.toFloat())
            }
            if (target.location != null && candidate.location != null &&
                GeoMath.distanceMeters(target.location, candidate.location) < 500
            ) {
                relationships += MemoryRelationship(target.id, candidate.id, RelationshipType.NEARBY)
            }
            relationships += MemoryRelationship(
                target.id, candidate.id,
                if (target.timestamp >= candidate.timestamp) RelationshipType.AFTER else RelationshipType.BEFORE
            )
        }
        relationshipRepository.linkAll(relationships)
    }
}
