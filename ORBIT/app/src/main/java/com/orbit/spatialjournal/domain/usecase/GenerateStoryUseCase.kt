package com.orbit.spatialjournal.domain.usecase

import com.orbit.spatialjournal.ai.AIAssistantProvider
import com.orbit.spatialjournal.core.model.GeneratedStory
import com.orbit.spatialjournal.domain.repository.MemoryRepository
import com.orbit.spatialjournal.domain.repository.TripRepository
import javax.inject.Inject

/** Backs "Story Mode" and the "AI Story Builder": turns a Trip, city or date range into a GeneratedStory. */
class GenerateStoryUseCase @Inject constructor(
    private val memoryRepository: MemoryRepository,
    private val tripRepository: TripRepository,
    private val assistant: AIAssistantProvider
) {
    suspend fun forTrip(tripId: String): GeneratedStory? {
        val trip = tripRepository.getTrip(tripId) ?: return null
        val memories = trip.memoryIds.mapNotNull { memoryRepository.getMemory(it) }
        return assistant.buildTripSummary(memories, trip.name)
    }

    suspend fun forDateRange(fromEpoch: Long, toEpoch: Long, title: String): GeneratedStory {
        val memories = memoryRepository.getInDateRange(fromEpoch, toEpoch)
        return assistant.buildStory(memories, title)
    }

    suspend fun forCity(city: String, title: String): GeneratedStory {
        val memories = memoryRepository.observeAll()
        // observeAll is a Flow; for a one-shot story build we take the first emission via first()
        val all = kotlinx.coroutines.flow.first(memories)
        val filtered = all.filter { it.city.equals(city, ignoreCase = true) }
        return assistant.buildStory(filtered, title)
    }
}
