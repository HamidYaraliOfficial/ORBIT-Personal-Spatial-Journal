package com.orbit.spatialjournal.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Physical attachments (photo/video/audio/document files) are normalized into their own
 * table so the Duplicate Detection Engine and Media Gallery can query across all
 * memories without deserializing every memory's attachmentsJson blob.
 */
@Entity(tableName = "attachments", indices = [Index("memoryId"), Index("sha256")])
data class AttachmentEntity(
    @PrimaryKey val id: String,
    val memoryId: String,
    val uri: String,
    val mimeType: String,
    val sizeBytes: Long,
    val durationMillis: Long?,
    val transcript: String?,
    val sha256: String?,
    val averageHash: Long?,
    val exifCapturedAt: Long?,
    val exifLatitude: Double?,
    val exifLongitude: Double?,
    val exifCameraMake: String?,
    val exifCameraModel: String?,
    val createdAt: Long
)
