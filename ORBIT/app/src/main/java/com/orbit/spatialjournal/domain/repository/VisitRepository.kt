package com.orbit.spatialjournal.domain.repository

import com.orbit.spatialjournal.core.model.VisitCandidate
import com.orbit.spatialjournal.core.model.VisitStatus
import kotlinx.coroutines.flow.Flow

interface VisitRepository {
    suspend fun saveCandidate(candidate: VisitCandidate)
    fun observeByStatus(status: VisitStatus): Flow<List<VisitCandidate>>
    fun observeAll(): Flow<List<VisitCandidate>>
    suspend fun confirm(id: String)
    suspend fun reject(id: String)
    suspend fun edit(candidate: VisitCandidate)
}
