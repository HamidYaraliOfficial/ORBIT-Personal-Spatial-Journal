package com.orbit.spatialjournal.data.local.entity

import androidx.room.Entity
import androidx.room.Fts4

/**
 * Shadow FTS4 table powering the Search Engine (full-text over title/description/notes/tags).
 * Kept in sync from MemoryDao.upsert via a manual insert/delete pair (Room does not
 * auto-sync content-less FTS tables across arbitrary primary key types).
 */
@Fts4(contentEntity = MemoryEntity::class)
@Entity(tableName = "memories_fts")
data class MemoryFtsEntity(
    val title: String,
    val description: String?,
    val notes: String?,
    val tagsJson: String,
    val placeName: String?,
    val city: String?,
    val country: String?
)
