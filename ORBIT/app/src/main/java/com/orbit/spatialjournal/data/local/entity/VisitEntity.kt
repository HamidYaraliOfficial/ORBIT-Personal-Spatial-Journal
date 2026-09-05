package com.orbit.spatialjournal.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.orbit.spatialjournal.core.model.VisitStatus

@Entity(tableName = "visits")
data class VisitEntity(
    @PrimaryKey val id: String,
    val latitude: Double,
    val longitude: Double,
    val placeId: String?,
    val placeNameGuess: String?,
    val arrivalAt: Long,
    val departureAt: Long?,
    val confidence: Float,
    val status: VisitStatus,
    val supportingPointCount: Int,
    val createdAt: Long
)
