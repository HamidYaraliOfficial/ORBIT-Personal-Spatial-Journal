package com.orbit.spatialjournal.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "places", indices = [Index("city"), Index("country")])
data class PlaceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val country: String?,
    val city: String?,
    val address: String?,
    val latitude: Double,
    val longitude: Double,
    val coverImageUri: String?,
    val firstVisitAt: Long?,
    val lastVisitAt: Long?,
    /** Serialized List<OpeningHoursEntry>, user-entered — see OpeningHoursUtils. */
    val openingHoursJson: String? = null,
    val isFavorite: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long
)
