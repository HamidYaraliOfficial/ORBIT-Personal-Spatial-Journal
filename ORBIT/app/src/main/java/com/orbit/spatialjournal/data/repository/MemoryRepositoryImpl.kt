package com.orbit.spatialjournal.data.repository

import com.orbit.spatialjournal.core.model.Memory
import com.orbit.spatialjournal.core.model.MemoryType
import com.orbit.spatialjournal.core.model.SearchFilters
import com.orbit.spatialjournal.core.util.DateTimeUtils
import com.orbit.spatialjournal.data.local.dao.MemoryDao
import com.orbit.spatialjournal.data.local.dao.TagDao
import com.orbit.spatialjournal.domain.repository.MemoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoryRepositoryImpl @Inject constructor(
    private val memoryDao: MemoryDao,
    private val tagDao: TagDao
) : MemoryRepository {

    override suspend fun saveMemory(memory: Memory) {
        memoryDao.upsertAndIndex(memory.toEntity())
        memory.tags.forEach { tagDao.touch(it) }
    }

    override suspend fun deleteMemory(id: String) = memoryDao.deleteById(id)

    override suspend fun getMemory(id: String): Memory? = memoryDao.getById(id)?.toDomain()

    override fun observeMemory(id: String): Flow<Memory?> =
        memoryDao.observeById(id).map { it?.toDomain() }

    override fun observeAll(): Flow<List<Memory>> =
        memoryDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeRecent(limit: Int): Flow<List<Memory>> =
        memoryDao.observeRecent(limit).map { list -> list.map { it.toDomain() } }

    override fun observeByType(type: MemoryType): Flow<List<Memory>> =
        memoryDao.observeByType(type).map { list -> list.map { it.toDomain() } }

    override fun observeByTrip(tripId: String): Flow<List<Memory>> =
        memoryDao.observeByTrip(tripId).map { list -> list.map { it.toDomain() } }

    override fun observeByPlace(placeId: String): Flow<List<Memory>> =
        memoryDao.observeByPlace(placeId).map { list -> list.map { it.toDomain() } }

    override fun observeInDateRange(fromEpoch: Long, toEpoch: Long): Flow<List<Memory>> =
        memoryDao.observeInRange(fromEpoch, toEpoch).map { list -> list.map { it.toDomain() } }

    override suspend fun getInDateRange(fromEpoch: Long, toEpoch: Long): List<Memory> =
        memoryDao.getInRange(fromEpoch, toEpoch).map { it.toDomain() }

    override fun observeInMapBounds(minLat: Double, maxLat: Double, minLon: Double, maxLon: Double): Flow<List<Memory>> =
        memoryDao.observeInBounds(minLat, maxLat, minLon, maxLon).map { list -> list.map { it.toDomain() } }

    override fun observeFavorites(): Flow<List<Memory>> =
        memoryDao.observeFavorites().map { list -> list.map { it.toDomain() } }

    override fun observeTotalCount(): Flow<Int> = memoryDao.observeTotalCount()

    override suspend fun setFavorite(id: String, favorite: Boolean) {
        val memory = getMemory(id) ?: return
        saveMemory(memory.copy(isFavorite = favorite, updatedAt = DateTimeUtils.nowMillis()))
    }

    override suspend fun setArchived(id: String, archived: Boolean) {
        val memory = getMemory(id) ?: return
        saveMemory(memory.copy(isArchived = archived, updatedAt = DateTimeUtils.nowMillis()))
    }

    override suspend fun search(filters: SearchFilters): List<Memory> {
        // Try FTS first for keyword queries; fall back to structured filtering (date/type/place)
        // when there is no free-text query, since FTS4 MATCH requires a non-empty term.
        val base = if (filters.query.isNotBlank()) {
            val sanitized = filters.query.trim().split(Regex("\\s+")).joinToString(" ") { "$it*" }
            runCatching { memoryDao.searchFts(sanitized) }.getOrDefault(emptyList()).map { it.toDomain() }
        } else {
            memoryDao.getInRange(filters.fromDate ?: 0L, filters.toDate ?: Long.MAX_VALUE).map { it.toDomain() }
        }

        return base.filter { m ->
            (filters.types.isEmpty() || m.type in filters.types) &&
                (filters.fromDate == null || m.timestamp >= filters.fromDate) &&
                (filters.toDate == null || m.timestamp <= filters.toDate) &&
                (filters.country == null || m.country.equals(filters.country, ignoreCase = true)) &&
                (filters.city == null || m.city.equals(filters.city, ignoreCase = true)) &&
                (filters.tag == null || m.tags.any { it.equals(filters.tag, ignoreCase = true) }) &&
                (filters.tripId == null || m.tripId == filters.tripId)
        }
    }
}
