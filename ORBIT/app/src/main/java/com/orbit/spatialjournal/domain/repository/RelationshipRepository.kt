package com.orbit.spatialjournal.domain.repository

import com.orbit.spatialjournal.core.model.MemoryRelationship
import kotlinx.coroutines.flow.Flow

interface RelationshipRepository {
    suspend fun link(relationship: MemoryRelationship)
    suspend fun linkAll(relationships: List<MemoryRelationship>)
    fun observeForMemory(memoryId: String): Flow<List<MemoryRelationship>>
    suspend fun getAll(): List<MemoryRelationship>
}
