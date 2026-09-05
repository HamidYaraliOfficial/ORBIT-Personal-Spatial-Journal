package com.orbit.spatialjournal.core.model

import kotlinx.serialization.Serializable

/** A single geographic point with an optional accuracy radius, in meters. */
@Serializable
data class GeoPoint(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float? = null
)

/**
 * The canonical, UI-facing Memory model. This is intentionally decoupled from the Room
 * entity (MemoryEntity) so the persistence schema can evolve without breaking every
 * screen that consumes a Memory.
 */
@Serializable
data class Memory(
    val id: String,
    val title: String,
    val description: String? = null,
    val type: MemoryType,
    val timestamp: Long,
    val startTime: Long? = null,
    val endTime: Long? = null,
    val location: GeoPoint? = null,
    val placeName: String? = null,
    val country: String? = null,
    val city: String? = null,
    val address: String? = null,
    val tags: List<String> = emptyList(),
    val attachments: List<Attachment> = emptyList(),
    val source: MemorySource = MemorySource.MANUAL_ENTRY,
    val mood: Mood? = null,
    val relatedMemoryIds: List<String> = emptyList(),
    val notes: String? = null,
    val metadata: Map<String, String> = emptyMap(),
    val version: Int = 1,
    val tripId: String? = null,
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
    val createdAt: Long = timestamp,
    val updatedAt: Long = timestamp
)

@Serializable
data class Attachment(
    val id: String,
    val uri: String,
    val mimeType: String,
    val sizeBytes: Long = 0,
    val durationMillis: Long? = null,
    val transcript: String? = null,
    val exif: ExifData? = null
)

@Serializable
data class ExifData(
    val capturedAt: Long? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val cameraMake: String? = null,
    val cameraModel: String? = null,
    val widthPx: Int? = null,
    val heightPx: Int? = null
)

@Serializable
data class OpeningHoursEntry(
    /** 1 = Monday ... 7 = Sunday (ISO-8601), matching kotlinx-datetime DayOfWeek.isoDayNumber. */
    val isoDayOfWeek: Int,
    val closedAllDay: Boolean = false,
    /** Minutes after midnight, e.g. 9:30 -> 570. Null when closedAllDay is true. */
    val openMinuteOfDay: Int? = null,
    val closeMinuteOfDay: Int? = null,
    val note: String? = null
)

data class OpeningStatus(
    val isOpenNow: Boolean,
    val currentSegmentEndsAtEpochMillis: Long?,
    val nextChangeAtEpochMillis: Long?,
    val minutesUntilNextChange: Long?
)

data class Place(
    val id: String,
    val name: String,
    val country: String? = null,
    val city: String? = null,
    val address: String? = null,
    val location: GeoPoint,
    val coverImageUri: String? = null,
    val memoryCount: Int = 0,
    val photoCount: Int = 0,
    val voiceCount: Int = 0,
    val firstVisitAt: Long? = null,
    val lastVisitAt: Long? = null,
    val topTags: List<String> = emptyList(),
    val openingHours: List<OpeningHoursEntry> = emptyList(),
    val isFavorite: Boolean = false
)

data class Trip(
    val id: String,
    val name: String,
    val coverImageUri: String? = null,
    val startDate: Long,
    val endDate: Long?,
    val status: TripStatus,
    val placeIds: List<String> = emptyList(),
    val memoryIds: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val isAutoSuggested: Boolean = false
)

data class VisitCandidate(
    val id: String,
    val location: GeoPoint,
    val placeName: String?,
    val arrivalAt: Long,
    val departureAt: Long?,
    val confidence: Float,
    val status: VisitStatus,
    val supportingPointCount: Int
)

data class MemoryRelationship(
    val fromMemoryId: String,
    val toMemoryId: String,
    val type: RelationshipType,
    val strength: Float = 1f
)

data class GraphNode(
    val id: String,
    val label: String,
    val kind: String, // "memory" | "place" | "trip" | "topic" | "event"
    val weight: Int = 1
)

data class GraphEdge(val fromId: String, val toId: String, val label: String)

data class SearchFilters(
    val query: String = "",
    val types: Set<MemoryType> = emptySet(),
    val fromDate: Long? = null,
    val toDate: Long? = null,
    val country: String? = null,
    val city: String? = null,
    val tag: String? = null,
    val tripId: String? = null
)

data class RecapData(
    val periodLabel: String,
    val fromDate: Long,
    val toDate: Long,
    val placesVisited: List<String>,
    val memoryCount: Int,
    val topTags: List<String>,
    val mostCapturedHour: Int?,
    val highlightMemoryIds: List<String>,
    val narrative: String
)

data class StorySection(val heading: String, val body: String, val memoryIds: List<String>)

data class GeneratedStory(
    val title: String,
    val introduction: String,
    val sections: List<StorySection>,
    val highlightPlaceIds: List<String>
)

data class DuplicateCandidate(
    val memoryIdA: String,
    val memoryIdB: String,
    val similarity: Float,
    val reason: String
)
