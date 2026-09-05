package com.orbit.spatialjournal.core.util

object Constants {
    const val DATABASE_NAME = "orbit_database.db"
    const val PREFERENCES_NAME = "orbit_settings"
    const val ENCRYPTED_PREFS_NAME = "orbit_secure_prefs"

    // Visit Detection Engine tuning
    const val VISIT_CLUSTER_RADIUS_METERS = 120.0
    const val VISIT_MIN_DWELL_MINUTES = 12
    const val VISIT_MIN_SUPPORTING_POINTS = 3

    // Location sampling (battery-aware scheduler baseline values; see BatteryAwareLocationScheduler)
    const val LOCATION_INTERVAL_NORMAL_MS = 5 * 60 * 1000L
    const val LOCATION_INTERVAL_SAVER_MS = 20 * 60 * 1000L
    const val LOCATION_FASTEST_INTERVAL_MS = 60 * 1000L

    // Geofencing
    const val GEOFENCE_DEFAULT_RADIUS_METERS = 150f
    const val GEOFENCE_EXPIRATION_MS = 7 * 24 * 60 * 60 * 1000L
    const val MAX_ACTIVE_GEOFENCES = 95 // Android's hard system limit is 100 per app.

    // Map clustering
    const val MAP_CLUSTER_MIN_ZOOM_FOR_INDIVIDUAL_MARKERS = 14f

    // Duplicate detection
    const val DUPLICATE_HAMMING_THRESHOLD = 6

    // WorkManager unique work names
    const val WORK_VISIT_DETECTION = "orbit_work_visit_detection"
    const val WORK_BACKUP = "orbit_work_backup"
    const val WORK_DUPLICATE_SCAN = "orbit_work_duplicate_scan"
    const val WORK_REMINDER_CHECK = "orbit_work_reminder_check"
    const val WORK_MEDIA_IMPORT = "orbit_work_media_import"

    const val NOTIFICATION_CHANNEL_REMINDERS = "orbit_reminders"
    const val NOTIFICATION_CHANNEL_VISITS = "orbit_visits"
    const val NOTIFICATION_CHANNEL_BACKUP = "orbit_backup"
}
