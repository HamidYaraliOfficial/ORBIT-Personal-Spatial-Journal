package com.orbit.spatialjournal.data.repository

import com.orbit.spatialjournal.core.model.VisitCandidate
import com.orbit.spatialjournal.core.model.VisitStatus
import com.orbit.spatialjournal.data.local.dao.VisitDao
import com.orbit.spatialjournal.data.local.entity.VisitEntity
import com.orbit.spatialjournal.domain.repository.VisitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VisitRepositoryImpl @Inject constructor(
    private val dao: VisitDao
) : VisitRepository {

    override suspend fun saveCandidate(candidate: VisitCandidate) = dao.upsert(candidate.toEntity())

    override fun observeByStatus(status: VisitStatus): Flow<List<VisitCandidate>> =
        dao.observeByStatus(status).map { list -> list.map { it.toDomain() } }

    override fun observeAll(): Flow<List<VisitCandidate>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun confirm(id: String) = dao.updateStatus(id, VisitStatus.CONFIRMED)
    override suspend fun reject(id: String) = dao.updateStatus(id, VisitStatus.REJECTED)
    override suspend fun edit(candidate: VisitCandidate) = dao.upsert(candidate.copy(status = VisitStatus.EDITED).toEntity())

    private fun VisitCandidate.toEntity() = VisitEntity(
        id = id, latitude = location.latitude, longitude = location.longitude, placeId = null,
        placeNameGuess = placeName, arrivalAt = arrivalAt, departureAt = departureAt,
        confidence = confidence, status = status, supportingPointCount = supportingPointCount,
        createdAt = System.currentTimeMillis()
    )

    private fun VisitEntity.toDomain() = VisitCandidate(
        id = id, location = com.orbit.spatialjournal.core.model.GeoPoint(latitude, longitude),
        placeName = placeNameGuess, arrivalAt = arrivalAt, departureAt = departureAt,
        confidence = confidence, status = status, supportingPointCount = supportingPointCount
    )
}
