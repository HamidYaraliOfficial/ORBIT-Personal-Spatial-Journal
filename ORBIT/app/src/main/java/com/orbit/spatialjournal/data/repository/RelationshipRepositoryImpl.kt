package com.orbit.spatialjournal.data.repository

import com.orbit.spatialjournal.core.model.MemoryRelationship
import com.orbit.spatialjournal.data.local.dao.RelationshipDao
import com.orbit.spatialjournal.data.local.entity.RelationshipEntity
import com.orbit.spatialjournal.domain.repository.RelationshipRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RelationshipRepositoryImpl @Inject constructor(
    private val dao: RelationshipDao
) : RelationshipRepository {

    override suspend fun link(relationship: MemoryRelationship) = dao.upsert(relationship.toEntity())

    override suspend fun linkAll(relationships: List<MemoryRelationship>) =
        dao.upsertAll(relationships.map { it.toEntity() })

    override fun observeForMemory(memoryId: String): Flow<List<MemoryRelationship>> =
        dao.observeForMemory(memoryId).map { list -> list.map { it.toDomain() } }

    override suspend fun getAll(): List<MemoryRelationship> = dao.getAll().map { it.toDomain() }

    private fun MemoryRelationship.toEntity() = RelationshipEntity(
        fromMemoryId = fromMemoryId, toMemoryId = toMemoryId, type = type,
        strength = strength, createdAt = System.currentTimeMillis()
    )

    private fun RelationshipEntity.toDomain() = MemoryRelationship(fromMemoryId, toMemoryId, type, strength)
}
