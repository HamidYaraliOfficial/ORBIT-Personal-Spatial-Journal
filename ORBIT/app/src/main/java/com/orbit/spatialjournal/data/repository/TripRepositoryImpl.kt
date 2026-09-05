package com.orbit.spatialjournal.data.repository

import com.orbit.spatialjournal.core.model.Trip
import com.orbit.spatialjournal.core.model.TripStatus
import com.orbit.spatialjournal.data.local.dao.MemoryDao
import com.orbit.spatialjournal.data.local.dao.TripDao
import com.orbit.spatialjournal.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TripRepositoryImpl @Inject constructor(
    private val tripDao: TripDao,
    private val memoryDao: MemoryDao
) : TripRepository {

    override suspend fun saveTrip(trip: Trip) = tripDao.upsert(trip.toEntity())

    override suspend fun deleteTrip(id: String) {
        tripDao.getById(id)?.let { tripDao.delete(it) }
    }

    override suspend fun getTrip(id: String): Trip? = tripDao.getById(id)?.let { enrich(it) }

    override fun observeTrip(id: String): Flow<Trip?> =
        tripDao.observeById(id).map { it?.let { entity -> enrich(entity) } }

    override fun observeAll(): Flow<List<Trip>> =
        tripDao.observeAll().map { list -> list.map { enrich(it) } }

    override fun observeByStatus(status: TripStatus): Flow<List<Trip>> =
        tripDao.observeByStatus(status).map { list -> list.map { enrich(it) } }

    override fun observeSuggested(): Flow<List<Trip>> =
        tripDao.observeSuggested().map { list -> list.map { enrich(it) } }

    override suspend fun acceptSuggestedTrip(id: String) {
        val trip = tripDao.getById(id) ?: return
        tripDao.upsert(trip.copy(status = TripStatus.COMPLETED, isAutoSuggested = false))
    }

    override suspend fun rejectSuggestedTrip(id: String) {
        tripDao.getById(id)?.let { tripDao.delete(it) }
    }

    private suspend fun enrich(entity: com.orbit.spatialjournal.data.local.entity.TripEntity): Trip {
        val memories = memoryDao.observeByTrip(entity.id).first()
        val placeIds = memories.mapNotNull { it.placeId }.distinct()
        return entity.toDomain(placeIds = placeIds, memoryIds = memories.map { it.id })
    }
}
