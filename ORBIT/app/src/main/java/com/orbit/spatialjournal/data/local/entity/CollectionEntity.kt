package com.orbit.spatialjournal.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.orbit.spatialjournal.core.model.CollectionKind

@Entity(tableName = "collections")
data class CollectionEntity(
    @PrimaryKey val id: String,
    val name: String,
    val kind: CollectionKind,
    val iconKey: String?,
    val createdAt: Long
)

@Entity(tableName = "collection_members", primaryKeys = ["collectionId", "memoryId"])
data class CollectionMemberEntity(
    val collectionId: String,
    val memoryId: String,
    val addedAt: Long
)
