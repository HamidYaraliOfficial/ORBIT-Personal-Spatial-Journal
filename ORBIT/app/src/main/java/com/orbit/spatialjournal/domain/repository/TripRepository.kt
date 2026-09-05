package com.orbit.spatialjournal.domain.repository

import com.orbit.spatialjournal.core.model.Trip
import com.orbit.spatialjournal.core.model.TripStatus
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    suspend fun saveTrip(trip: Trip)
    suspend fun deleteTrip(id: String)
    suspend fun getTrip(id: String): Trip?
    fun observeTrip(id: String): Flow<Trip?>
    fun observeAll(): Flow<List<Trip>>
    fun observeByStatus(status: TripStatus): Flow<List<Trip>>
    fun observeSuggested(): Flow<List<Trip>>
    suspend fun acceptSuggestedTrip(id: String)
    suspend fun rejectSuggestedTrip(id: String)
}
