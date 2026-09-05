package com.orbit.spatialjournal.domain.repository

import com.orbit.spatialjournal.core.model.OpeningHoursEntry
import com.orbit.spatialjournal.core.model.Place
import kotlinx.coroutines.flow.Flow

interface PlaceRepository {
    suspend fun savePlace(place: Place)
    suspend fun getPlace(id: String): Place?
    fun observePlace(id: String): Flow<Place?>
    fun observeAll(): Flow<List<Place>>
    fun observeFavorites(): Flow<List<Place>>
    fun observeByCountry(country: String): Flow<List<Place>>
    fun observeDistinctCountries(): Flow<List<String>>
    suspend fun search(query: String): List<Place>
    suspend fun setFavorite(id: String, favorite: Boolean)
    suspend fun updateOpeningHours(id: String, hours: List<OpeningHoursEntry>)
    suspend fun findOrCreateNearby(lat: Double, lon: Double, name: String, radiusMeters: Double = 80.0): Place
}
