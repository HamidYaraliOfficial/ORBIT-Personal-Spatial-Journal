package com.orbit.spatialjournal.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AnnotationShape { PIN, CIRCLE, ROUTE, AREA, LINE }

@Entity(tableName = "map_annotations")
data class MapAnnotationEntity(
    @PrimaryKey val id: String,
    val shape: AnnotationShape,
    val label: String?,
    val note: String?,
    /** Serialized List<GeoPoint>: 1 point for PIN/CIRCLE, 2+ for ROUTE/AREA/LINE. */
    val pointsJson: String,
    val radiusMeters: Float? = null,
    val colorHex: String = "#3B82F6",
    val createdAt: Long
)
