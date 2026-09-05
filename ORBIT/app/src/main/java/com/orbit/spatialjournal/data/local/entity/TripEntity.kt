package com.orbit.spatialjournal.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.orbit.spatialjournal.core.model.TripStatus

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey val id: String,
    val name: String,
    val coverImageUri: String?,
    val startDate: Long,
    val endDate: Long?,
    val status: TripStatus,
    val tagsJson: String,
    val isAutoSuggested: Boolean,
    val expensesJson: String? = null, // optional manual expense entries, see TripDetail
    val createdAt: Long,
    val updatedAt: Long
)
