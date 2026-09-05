package com.orbit.spatialjournal.domain.usecase

import com.orbit.spatialjournal.core.model.Memory
import com.orbit.spatialjournal.core.util.IdGenerator
import com.orbit.spatialjournal.domain.repository.MemoryRepository
import com.orbit.spatialjournal.domain.repository.PlaceRepository
import javax.inject.Inject

/**
 * Backs Smart Memory Capture / Quick Capture: fills in an id + timestamps if missing,
 * resolves (or creates) a Place when a location is attached, and persists the Memory.
 */
class CreateMemoryUseCase @Inject constructor(
    private val memoryRepository: MemoryRepository,
    private val placeRepository: PlaceRepository
) {
    suspend operator fun invoke(draft: Memory): Memory {
        val now = System.currentTimeMillis()
        var memory = draft.copy(
            id = draft.id.ifBlank { IdGenerator.newId() },
            timestamp = if (draft.timestamp == 0L) now else draft.timestamp,
            createdAt = now, updatedAt = now
        )

        if (memory.location != null && memory.placeName == null) {
            val place = placeRepository.findOrCreateNearby(
                lat = memory.location.latitude, lon = memory.location.longitude,
                name = memory.title
            )
            memory = memory.copy(placeName = place.name, city = place.city, country = place.country)
        }

        memoryRepository.saveMemory(memory)
        return memory
    }
}
