package com.orbit.spatialjournal.domain.repository

import kotlinx.coroutines.flow.Flow

data class JournalBlock(
    val type: String, // "text" | "heading" | "checklist" | "image" | "video" | "voice" | "link" | "quote" | "location"
    val content: String,
    val checked: Boolean? = null,
    val refId: String? = null // memoryId, attachmentId, or placeId depending on type
)

data class JournalEntry(
    val id: String,
    val memoryId: String,
    val blocks: List<JournalBlock>,
    val linkedMemoryIds: List<String>,
    val linkedPlaceIds: List<String>,
    val updatedAt: Long
)

interface JournalRepository {
    suspend fun save(entry: JournalEntry)
    fun observe(id: String): Flow<JournalEntry?>
    fun observeAll(): Flow<List<JournalEntry>>
    suspend fun getByMemoryId(memoryId: String): JournalEntry?
}
