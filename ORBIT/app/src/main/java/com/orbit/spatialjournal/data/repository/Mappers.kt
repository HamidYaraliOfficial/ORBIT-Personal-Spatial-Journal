package com.orbit.spatialjournal.data.repository

import com.orbit.spatialjournal.core.model.*
import com.orbit.spatialjournal.data.local.entity.MemoryEntity
import com.orbit.spatialjournal.data.local.entity.PlaceEntity
import com.orbit.spatialjournal.data.local.entity.TripEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val mapperJson = Json { ignoreUnknownKeys = true; encodeDefaults = true }

fun Memory.toEntity(): MemoryEntity = MemoryEntity(
    id = id, title = title, description = description, type = type, timestamp = timestamp,
    startTime = startTime, endTime = endTime,
    latitude = location?.latitude, longitude = location?.longitude, accuracyMeters = location?.accuracyMeters,
    placeName = placeName, country = country, city = city, address = address,
    tagsJson = mapperJson.encodeToString(tags),
    attachmentsJson = mapperJson.encodeToString(attachments),
    source = source, mood = mood,
    relatedMemoryIdsJson = mapperJson.encodeToString(relatedMemoryIds),
    notes = notes, metadataJson = mapperJson.encodeToString(metadata), version = version,
    tripId = tripId, placeId = null, isFavorite = isFavorite, isArchived = isArchived,
    createdAt = createdAt, updatedAt = updatedAt
)

fun MemoryEntity.toDomain(): Memory = Memory(
    id = id, title = title, description = description, type = type, timestamp = timestamp,
    startTime = startTime, endTime = endTime,
    location = if (latitude != null && longitude != null) GeoPoint(latitude, longitude, accuracyMeters) else null,
    placeName = placeName, country = country, city = city, address = address,
    tags = runCatching { mapperJson.decodeFromString<List<String>>(tagsJson) }.getOrDefault(emptyList()),
    attachments = runCatching { mapperJson.decodeFromString<List<Attachment>>(attachmentsJson) }.getOrDefault(emptyList()),
    source = source, mood = mood,
    relatedMemoryIds = runCatching { mapperJson.decodeFromString<List<String>>(relatedMemoryIdsJson) }.getOrDefault(emptyList()),
    notes = notes,
    metadata = runCatching { mapperJson.decodeFromString<Map<String, String>>(metadataJson) }.getOrDefault(emptyMap()),
    version = version, tripId = tripId, isFavorite = isFavorite, isArchived = isArchived,
    createdAt = createdAt, updatedAt = updatedAt
)

fun Place.toEntity(): PlaceEntity = PlaceEntity(
    id = id, name = name, country = country, city = city, address = address,
    latitude = location.latitude, longitude = location.longitude, coverImageUri = coverImageUri,
    firstVisitAt = firstVisitAt, lastVisitAt = lastVisitAt,
    openingHoursJson = if (openingHours.isEmpty()) null else mapperJson.encodeToString(openingHours),
    isFavorite = isFavorite, createdAt = firstVisitAt ?: System.currentTimeMillis(),
    updatedAt = System.currentTimeMillis()
)

fun PlaceEntity.toDomain(memoryCount: Int = 0, photoCount: Int = 0, voiceCount: Int = 0, topTags: List<String> = emptyList()): Place = Place(
    id = id, name = name, country = country, city = city, address = address,
    location = GeoPoint(latitude, longitude), coverImageUri = coverImageUri,
    memoryCount = memoryCount, photoCount = photoCount, voiceCount = voiceCount,
    firstVisitAt = firstVisitAt, lastVisitAt = lastVisitAt, topTags = topTags,
    openingHours = openingHoursJson?.let {
        runCatching { mapperJson.decodeFromString<List<OpeningHoursEntry>>(it) }.getOrNull()
    } ?: emptyList(),
    isFavorite = isFavorite
)

fun Trip.toEntity(): TripEntity = TripEntity(
    id = id, name = name, coverImageUri = coverImageUri, startDate = startDate, endDate = endDate,
    status = status, tagsJson = mapperJson.encodeToString(tags), isAutoSuggested = isAutoSuggested,
    createdAt = startDate, updatedAt = System.currentTimeMillis()
)

fun TripEntity.toDomain(placeIds: List<String> = emptyList(), memoryIds: List<String> = emptyList()): Trip = Trip(
    id = id, name = name, coverImageUri = coverImageUri, startDate = startDate, endDate = endDate,
    status = status, placeIds = placeIds, memoryIds = memoryIds,
    tags = runCatching { mapperJson.decodeFromString<List<String>>(tagsJson) }.getOrDefault(emptyList()),
    isAutoSuggested = isAutoSuggested
)
