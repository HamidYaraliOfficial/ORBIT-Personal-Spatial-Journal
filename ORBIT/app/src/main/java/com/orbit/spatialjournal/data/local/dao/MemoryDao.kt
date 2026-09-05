package com.orbit.spatialjournal.data.local.dao

import androidx.room.*
import com.orbit.spatialjournal.core.model.MemoryType
import com.orbit.spatialjournal.data.local.entity.MemoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(memory: MemoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(memories: List<MemoryEntity>)

    @Delete
    suspend fun delete(memory: MemoryEntity)

    @Query("DELETE FROM memories WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM memories WHERE id = :id")
    suspend fun getById(id: String): MemoryEntity?

    @Query("SELECT * FROM memories WHERE id = :id")
    fun observeById(id: String): Flow<MemoryEntity?>

    @Query("SELECT * FROM memories WHERE isArchived = 0 ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE isArchived = 0 ORDER BY timestamp DESC LIMIT :limit")
    fun observeRecent(limit: Int = 20): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE type = :type AND isArchived = 0 ORDER BY timestamp DESC")
    fun observeByType(type: MemoryType): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE tripId = :tripId ORDER BY timestamp ASC")
    fun observeByTrip(tripId: String): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE placeId = :placeId ORDER BY timestamp DESC")
    fun observeByPlace(placeId: String): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE timestamp BETWEEN :fromEpoch AND :toEpoch ORDER BY timestamp ASC")
    fun observeInRange(fromEpoch: Long, toEpoch: Long): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE timestamp BETWEEN :fromEpoch AND :toEpoch ORDER BY timestamp ASC")
    suspend fun getInRange(fromEpoch: Long, toEpoch: Long): List<MemoryEntity>

    @Query(
        """SELECT * FROM memories WHERE latitude IS NOT NULL AND longitude IS NOT NULL
           AND latitude BETWEEN :minLat AND :maxLat AND longitude BETWEEN :minLon AND :maxLon
           AND isArchived = 0"""
    )
    fun observeInBounds(minLat: Double, maxLat: Double, minLon: Double, maxLon: Double): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun observeFavorites(): Flow<List<MemoryEntity>>

    @Query("SELECT DISTINCT country FROM memories WHERE country IS NOT NULL")
    fun observeDistinctCountries(): Flow<List<String>>

    @Query("SELECT DISTINCT city FROM memories WHERE city IS NOT NULL")
    fun observeDistinctCities(): Flow<List<String>>

    @Query("SELECT COUNT(*) FROM memories WHERE isArchived = 0")
    fun observeTotalCount(): Flow<Int>

    @Query(
        """SELECT memories.* FROM memories
           JOIN memories_fts ON memories.rowid = memories_fts.rowid
           WHERE memories_fts MATCH :ftsQuery ORDER BY memories.timestamp DESC"""
    )
    suspend fun searchFts(ftsQuery: String): List<MemoryEntity>

    @Query("INSERT INTO memories_fts(rowid, title, description, notes, tagsJson, placeName, city, country) " +
        "SELECT rowid, title, description, notes, tagsJson, placeName, city, country FROM memories WHERE id = :id")
    suspend fun syncFtsRowForMemory(id: String)

    @Transaction
    suspend fun upsertAndIndex(memory: MemoryEntity) {
        upsert(memory)
        syncFtsRowForMemory(memory.id)
    }
}
