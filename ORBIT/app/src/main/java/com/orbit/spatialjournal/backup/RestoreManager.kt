package com.orbit.spatialjournal.backup

import com.orbit.spatialjournal.domain.repository.MemoryRepository
import javax.inject.Inject

/** Applies a parsed BackupPayload back into the live database, memory by memory (idempotent upsert). */
class RestoreManager @Inject constructor(
    private val memoryRepository: MemoryRepository
) {
    suspend fun restore(payload: BackupPayload) {
        payload.memories.forEach { memoryRepository.saveMemory(it) }
    }
}
