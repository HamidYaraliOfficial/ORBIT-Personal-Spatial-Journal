package com.orbit.spatialjournal.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey val id: String,
    val memoryId: String, // every journal entry is backed by a JOURNAL-type Memory
    /** Rich content stored as a block list, see JournalBlock in JournalModels.kt */
    val contentBlocksJson: String,
    val linkedMemoryIdsJson: String,
    val linkedLocationIdsJson: String,
    val createdAt: Long,
    val updatedAt: Long
)
