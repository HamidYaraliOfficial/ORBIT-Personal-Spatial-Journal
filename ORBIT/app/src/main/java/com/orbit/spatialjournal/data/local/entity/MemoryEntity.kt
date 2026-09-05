package com.orbit.spatialjournal.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.orbit.spatialjournal.core.model.MemoryType
import com.orbit.spatialjournal.core.model.Mood
import com.orbit.spatialjournal.core.model.MemorySource

@Entity(
    tableName = "memories",
    indices = [
        Index("timestamp"), Index("type"), Index("tripId"),
        Index("city"), Index("country"), Index("latitude", "longitude")
    ]
)
data class MemoryEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String?,
    val type: MemoryType,
    val timestamp: Long,
    val startTime: Long?,
    val endTime: Long?,
    val latitude: Double?,
    val longitude: Double?,
    val accuracyMeters: Float?,
    val placeName: String?,
    val country: String?,
    val city: String?,
    val address: String?,
    /** Comma-free JSON array of tag strings; the FTS shadow table indexes this too. */
    val tagsJson: String,
    val attachmentsJson: String,
    val source: MemorySource,
    val mood: Mood?,
    val relatedMemoryIdsJson: String,
    val notes: String?,
    val metadataJson: String,
    val version: Int,
    val tripId: String?,
    val placeId: String?,
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long
)
