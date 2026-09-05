package com.orbit.spatialjournal.domain.repository

import com.orbit.spatialjournal.core.model.*
import kotlinx.coroutines.flow.Flow

interface MemoryRepository {
    suspend fun saveMemory(memory: Memory)
    suspend fun deleteMemory(id: String)
    suspend fun getMemory(id: String): Memory?
    fun observeMemory(id: String): Flow<Memory?>
    fun observeAll(): Flow<List<Memory>>
    fun observeRecent(limit: Int = 20): Flow<List<Memory>>
    fun observeByType(type: MemoryType): Flow<List<Memory>>
    fun observeByTrip(tripId: String): Flow<List<Memory>>
    fun observeByPlace(placeId: String): Flow<List<Memory>>
    fun observeInDateRange(fromEpoch: Long, toEpoch: Long): Flow<List<Memory>>
    suspend fun getInDateRange(fromEpoch: Long, toEpoch: Long): List<Memory>
    fun observeInMapBounds(minLat: Double, maxLat: Double, minLon: Double, maxLon: Double): Flow<List<Memory>>
    fun observeFavorites(): Flow<List<Memory>>
    fun observeTotalCount(): Flow<Int>
    suspend fun setFavorite(id: String, favorite: Boolean)
    suspend fun setArchived(id: String, archived: Boolean)
    suspend fun search(filters: SearchFilters): List<Memory>
}
