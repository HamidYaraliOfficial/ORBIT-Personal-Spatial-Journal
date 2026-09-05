package com.orbit.spatialjournal.data.repository

import com.orbit.spatialjournal.data.local.dao.JournalDao
import com.orbit.spatialjournal.data.local.entity.JournalEntryEntity
import com.orbit.spatialjournal.domain.repository.JournalEntry
import com.orbit.spatialjournal.domain.repository.JournalBlock
import com.orbit.spatialjournal.domain.repository.JournalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val journalJson = Json { ignoreUnknownKeys = true; encodeDefaults = true }

@kotlinx.serialization.Serializable
private data class JournalBlockDto(
    val type: String, val content: String,
    val checked: Boolean? = null, val refId: String? = null
)

private fun JournalBlock.toDto() = JournalBlockDto(type, content, checked, refId)
private fun JournalBlockDto.toDomain() = JournalBlock(type, content, checked, refId)

private fun JournalEntryEntity.toDomain(): JournalEntry = JournalEntry(
    id = id, memoryId = memoryId,
    blocks = runCatching { journalJson.decodeFromString<List<JournalBlockDto>>(contentBlocksJson) }
        .getOrDefault(emptyList()).map { it.toDomain() },
    linkedMemoryIds = runCatching { journalJson.decodeFromString<List<String>>(linkedMemoryIdsJson) }.getOrDefault(emptyList()),
    linkedPlaceIds = runCatching { journalJson.decodeFromString<List<String>>(linkedLocationIdsJson) }.getOrDefault(emptyList()),
    updatedAt = updatedAt
)

private fun JournalEntry.toEntity(): JournalEntryEntity = JournalEntryEntity(
    id = id, memoryId = memoryId,
    contentBlocksJson = journalJson.encodeToString(blocks.map { it.toDto() }),
    linkedMemoryIdsJson = journalJson.encodeToString(linkedMemoryIds),
    linkedLocationIdsJson = journalJson.encodeToString(linkedPlaceIds),
    createdAt = updatedAt, updatedAt = updatedAt
)

@Singleton
class JournalRepositoryImpl @Inject constructor(
    private val journalDao: JournalDao
) : JournalRepository {
    override suspend fun save(entry: JournalEntry) = journalDao.upsert(entry.toEntity())
    override fun observe(id: String): Flow<JournalEntry?> = journalDao.observeById(id).map { it?.toDomain() }
    override fun observeAll(): Flow<List<JournalEntry>> = journalDao.observeAll().map { it.map { e -> e.toDomain() } }
    override suspend fun getByMemoryId(memoryId: String): JournalEntry? = journalDao.getByMemoryId(memoryId)?.toDomain()
}
