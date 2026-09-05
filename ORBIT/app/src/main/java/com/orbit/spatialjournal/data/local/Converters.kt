package com.orbit.spatialjournal.data.local

import androidx.room.TypeConverter
import com.orbit.spatialjournal.core.model.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/** Central Room TypeConverters. All enums are stored as their name() string for readability
 * in ad-hoc SQL debugging; all lists/maps are stored as compact JSON via kotlinx.serialization. */
class Converters {

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    // ---- Enums ----
    @TypeConverter fun fromMemoryType(v: MemoryType) = v.name
    @TypeConverter fun toMemoryType(v: String) = MemoryType.valueOf(v)

    @TypeConverter fun fromMood(v: Mood?) = v?.name
    @TypeConverter fun toMood(v: String?) = v?.let { Mood.valueOf(it) }

    @TypeConverter fun fromMemorySource(v: MemorySource) = v.name
    @TypeConverter fun toMemorySource(v: String) = MemorySource.valueOf(v)

    @TypeConverter fun fromRelationshipType(v: RelationshipType) = v.name
    @TypeConverter fun toRelationshipType(v: String) = RelationshipType.valueOf(v)

    @TypeConverter fun fromVisitStatus(v: VisitStatus) = v.name
    @TypeConverter fun toVisitStatus(v: String) = VisitStatus.valueOf(v)

    @TypeConverter fun fromTripStatus(v: TripStatus) = v.name
    @TypeConverter fun toTripStatus(v: String) = TripStatus.valueOf(v)

    @TypeConverter fun fromReminderKind(v: ReminderKind) = v.name
    @TypeConverter fun toReminderKind(v: String) = ReminderKind.valueOf(v)

    @TypeConverter fun fromCollectionKind(v: CollectionKind) = v.name
    @TypeConverter fun toCollectionKind(v: String) = CollectionKind.valueOf(v)

    @TypeConverter
    fun fromAnnotationShape(v: com.orbit.spatialjournal.data.local.entity.AnnotationShape) = v.name
    @TypeConverter
    fun toAnnotationShape(v: String) = com.orbit.spatialjournal.data.local.entity.AnnotationShape.valueOf(v)

    // ---- Collections ----
    @TypeConverter fun fromStringList(v: List<String>): String = json.encodeToString(v)
    @TypeConverter fun toStringList(v: String): List<String> =
        runCatching { json.decodeFromString<List<String>>(v) }.getOrDefault(emptyList())

    @TypeConverter fun fromStringMap(v: Map<String, String>): String = json.encodeToString(v)
    @TypeConverter fun toStringMap(v: String): Map<String, String> =
        runCatching { json.decodeFromString<Map<String, String>>(v) }.getOrDefault(emptyMap())

    @TypeConverter fun fromAttachmentList(v: List<Attachment>): String = json.encodeToString(v)
    @TypeConverter fun toAttachmentList(v: String): List<Attachment> =
        runCatching { json.decodeFromString<List<Attachment>>(v) }.getOrDefault(emptyList())

    @TypeConverter fun fromOpeningHoursList(v: List<OpeningHoursEntry>?): String? =
        v?.let { json.encodeToString(it) }
    @TypeConverter fun toOpeningHoursList(v: String?): List<OpeningHoursEntry>? =
        v?.let { runCatching { json.decodeFromString<List<OpeningHoursEntry>>(it) }.getOrNull() }

    @TypeConverter fun fromGeoPointList(v: List<GeoPoint>): String = json.encodeToString(v)
    @TypeConverter fun toGeoPointList(v: String): List<GeoPoint> =
        runCatching { json.decodeFromString<List<GeoPoint>>(v) }.getOrDefault(emptyList())
}
