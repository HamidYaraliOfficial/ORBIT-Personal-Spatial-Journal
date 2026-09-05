package com.orbit.spatialjournal.core.model

/** Every kind of thing a user can capture into ORBIT. */
enum class MemoryType {
    PHOTO, VIDEO, VOICE, NOTE, JOURNAL, EVENT, PLACE, TRIP, TASK, BOOKMARK, DOCUMENT, CUSTOM
}

/** Optional, user-entered mood tag for a Memory. Never inferred automatically. */
enum class Mood {
    GREAT, GOOD, NEUTRAL, LOW, DIFFICULT
}

/** Where a Memory came from, for provenance and de-duplication logic. */
enum class MemorySource {
    CAPTURED_IN_APP, IMPORTED_GALLERY, IMPORTED_FILE, SHARE_SHEET, WIDGET,
    QUICK_SETTINGS_TILE, VOICE_CAPTURE, CALENDAR_SYNC, MANUAL_ENTRY
}

/** Kinds of relationship the Memory Relationship Engine can create between two memories. */
enum class RelationshipType {
    SAME_PLACE, SAME_DAY, SAME_TRIP, RELATED_TOPIC, BEFORE, AFTER,
    NEARBY, CREATED_FROM, MENTIONED_IN, PART_OF_EVENT
}

/** How confident the Visit Detection Engine is about a suggested visit. Never treated as fact. */
enum class VisitStatus {
    SUGGESTED, CONFIRMED, EDITED, REJECTED
}

enum class TripStatus {
    PLANNED, ONGOING, COMPLETED, SUGGESTED
}

/** User-controlled granularity for how much location data ORBIT is allowed to collect. */
enum class LocationMode {
    OFF,                // No location is ever read.
    MANUAL,             // Location is only read when the user explicitly taps "capture location".
    WHILE_USING,        // Location is read only while the app is in the foreground.
    SMART_CONTEXT,      // Coarse, battery-aware background sampling used for Visit Detection.
    BACKGROUND_REMINDERS// Adds geofence-based Place Reminders on top of Smart Context.
}

enum class ThemeMode { LIGHT, DARK, AMOLED, SYSTEM }

enum class AccentStyle { WINDOWS11, RED, BLUE }

enum class AppLanguage(val tag: String) {
    ENGLISH("en"), PERSIAN("fa"), CHINESE("zh")
}

enum class MapStyleOption { STANDARD, DARK, MINIMAL, TRAVEL }

enum class ExportFormat { JSON, CSV, ZIP, PDF, GEOJSON, KML }

enum class TimelineGranularity { DAY, WEEK, MONTH, YEAR, TRAVEL }

enum class ReminderKind { MEMORY, PLACE, TRIP, TASK, LOCATION }

enum class CollectionKind { FAVORITE_PLACES, BEST_CAFES, TRIPS, UNIVERSITY, PROJECTS, CUSTOM }
