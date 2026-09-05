package com.orbit.spatialjournal.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.orbit.spatialjournal.core.model.ReminderKind

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey val id: String,
    val kind: ReminderKind,
    val title: String,
    val relatedMemoryId: String?,
    val relatedPlaceId: String?,
    val relatedTripId: String?,
    val triggerAtEpochMillis: Long?, // time-based reminder
    val geofenceLatitude: Double?,   // location-based reminder
    val geofenceLongitude: Double?,
    val geofenceRadiusMeters: Float?,
    val isEnabled: Boolean = true,
    val isFired: Boolean = false,
    val createdAt: Long
)
