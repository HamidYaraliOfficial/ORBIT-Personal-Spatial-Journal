package com.orbit.spatialjournal.data.local.dao

import androidx.room.*
import com.orbit.spatialjournal.data.local.entity.JournalEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: JournalEntryEntity)

    @Query("SELECT * FROM journal_entries WHERE id = :id")
    fun observeById(id: String): Flow<JournalEntryEntity?>

    @Query("SELECT * FROM journal_entries WHERE memoryId = :memoryId")
    suspend fun getByMemoryId(memoryId: String): JournalEntryEntity?

    @Query("SELECT * FROM journal_entries ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<JournalEntryEntity>>
}
