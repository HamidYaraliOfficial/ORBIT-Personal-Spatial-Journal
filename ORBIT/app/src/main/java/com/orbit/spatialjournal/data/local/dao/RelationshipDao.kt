package com.orbit.spatialjournal.data.local.dao

import androidx.room.*
import com.orbit.spatialjournal.data.local.entity.RelationshipEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RelationshipDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(relationship: RelationshipEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(relationships: List<RelationshipEntity>)

    @Query("SELECT * FROM memory_relationships WHERE fromMemoryId = :memoryId OR toMemoryId = :memoryId")
    fun observeForMemory(memoryId: String): Flow<List<RelationshipEntity>>

    @Query("SELECT * FROM memory_relationships")
    suspend fun getAll(): List<RelationshipEntity>

    @Query("DELETE FROM memory_relationships WHERE fromMemoryId = :memoryId OR toMemoryId = :memoryId")
    suspend fun deleteForMemory(memoryId: String)
}
