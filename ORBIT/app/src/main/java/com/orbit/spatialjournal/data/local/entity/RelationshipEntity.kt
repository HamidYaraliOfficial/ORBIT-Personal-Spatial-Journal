package com.orbit.spatialjournal.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import com.orbit.spatialjournal.core.model.RelationshipType

@Entity(
    tableName = "memory_relationships",
    primaryKeys = ["fromMemoryId", "toMemoryId", "type"],
    indices = [Index("fromMemoryId"), Index("toMemoryId")]
)
data class RelationshipEntity(
    val fromMemoryId: String,
    val toMemoryId: String,
    val type: RelationshipType,
    val strength: Float = 1f,
    val createdAt: Long
)
