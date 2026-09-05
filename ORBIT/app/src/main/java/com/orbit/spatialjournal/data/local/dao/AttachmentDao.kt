package com.orbit.spatialjournal.data.local.dao

import androidx.room.*
import com.orbit.spatialjournal.data.local.entity.AttachmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttachmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(attachment: AttachmentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(attachments: List<AttachmentEntity>)

    @Query("SELECT * FROM attachments WHERE memoryId = :memoryId")
    fun observeForMemory(memoryId: String): Flow<List<AttachmentEntity>>

    @Query("SELECT * FROM attachments WHERE mimeType LIKE 'image/%' ORDER BY createdAt DESC")
    fun observeAllPhotos(): Flow<List<AttachmentEntity>>

    @Query("SELECT * FROM attachments WHERE mimeType LIKE 'video/%' ORDER BY createdAt DESC")
    fun observeAllVideos(): Flow<List<AttachmentEntity>>

    @Query("SELECT * FROM attachments WHERE sha256 = :hash")
    suspend fun findBySha256(hash: String): List<AttachmentEntity>

    @Query("SELECT * FROM attachments WHERE averageHash IS NOT NULL")
    suspend fun getAllWithPerceptualHash(): List<AttachmentEntity>
}
