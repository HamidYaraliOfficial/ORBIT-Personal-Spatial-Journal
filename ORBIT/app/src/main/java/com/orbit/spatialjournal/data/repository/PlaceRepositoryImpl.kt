package com.orbit.spatialjournal.data.repository

import com.orbit.spatialjournal.core.model.GeoPoint
import com.orbit.spatialjournal.core.model.OpeningHoursEntry
import com.orbit.spatialjournal.core.model.Place
import com.orbit.spatialjournal.core.util.GeoMath
import com.orbit.spatialjournal.core.util.IdGenerator
import com.orbit.spatialjournal.data.local.dao.AttachmentDao
import com.orbit.spatialjournal.data.local.dao.MemoryDao
import com.orbit.spatialjournal.data.local.dao.PlaceDao
import com.orbit.spatialjournal.domain.repository.PlaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaceRepositoryImpl @Inject constructor(
    private val placeDao: PlaceDao,
    private val memoryDao: MemoryDao
) : PlaceRepository {

    override suspend fun savePlace(place: Place) = placeDao.upsert(place.toEntity())

    override suspend fun getPlace(id: String): Place? = placeDao.getById(id)?.let { enrich(it) }

    override fun observePlace(id: String): Flow<Place?> =
        placeDao.observeById(id).map { it?.let { entity -> enrich(entity) } }

    override fun observeAll(): Flow<List<Place>> =
        placeDao.observeAll().map { list -> list.map { enrich(it) } }

    override fun observeFavorites(): Flow<List<Place>> =
        placeDao.observeFavorites().map { list -> list.map { enrich(it) } }

    override fun observeByCountry(country: String): Flow<List<Place>> =
        placeDao.observeByCountry(country).map { list -> list.map { enrich(it) } }

    override fun observeDistinctCountries(): Flow<List<String>> = placeDao.observeDistinctCountries()

    override suspend fun search(query: String): List<Place> = placeDao.search(query).map { enrich(it) }

    override suspend fun setFavorite(id: String, favorite: Boolean) {
        val place = placeDao.getById(id) ?: return
        placeDao.upsert(place.copy(isFavorite = favorite, updatedAt = System.currentTimeMillis()))
    }

    override suspend fun updateOpeningHours(id: String, hours: List<OpeningHoursEntry>) {
        val place = placeDao.getById(id) ?: return
        val updated = place.toDomain().copy(openingHours = hours)
        savePlace(updated)
    }

    override suspend fun findOrCreateNearby(lat: Double, lon: Double, name: String, radiusMeters: Double): Place {
        val nearby = placeDao.getInBounds(lat - 0.01, lat + 0.01, lon - 0.01, lon + 0.01)
        val target = GeoPoint(lat, lon)
        val match = nearby.firstOrNull { GeoMath.distanceMeters(target, GeoPoint(it.latitude, it.longitude)) <= radiusMeters }
        if (match != null) return enrich(match)

        val newPlace = Place(id = IdGenerator.newId(), name = name, location = target, firstVisitAt = System.currentTimeMillis())
        savePlace(newPlace)
        return newPlace
    }

    /** Attaches derived, read-only stats (memory/photo/voice counts, top tags) to a stored Place row. */
    private suspend fun enrich(entity: com.orbit.spatialjournal.data.local.entity.PlaceEntity): Place {
        val memories = memoryDao.observeByPlace(entity.id).first()
        val photoCount = memories.count { it.type == com.orbit.spatialjournal.core.model.MemoryType.PHOTO }
        val voiceCount = memories.count { it.type == com.orbit.spatialjournal.core.model.MemoryType.VOICE }
        val topTags = memories.flatMap { m ->
            runCatching { kotlinx.serialization.json.Json.decodeFromString<List<String>>(m.tagsJson) }.getOrDefault(emptyList())
        }.groupingBy { it }.eachCount().entries.sortedByDescending { it.value }.take(5).map { it.key }

        return entity.toDomain(memoryCount = memories.size, photoCount = photoCount, voiceCount = voiceCount, topTags = topTags)
    }
}
