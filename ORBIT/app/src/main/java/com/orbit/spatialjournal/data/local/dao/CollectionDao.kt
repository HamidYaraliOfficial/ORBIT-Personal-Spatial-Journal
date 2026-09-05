package com.orbit.spatialjournal.data.local.dao

import androidx.room.*
import com.orbit.spatialjournal.data.local.entity.CollectionEntity
import com.orbit.spatialjournal.data.local.entity.CollectionMemberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(collection: CollectionEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addMember(member: CollectionMemberEntity)

    @Query("DELETE FROM collection_members WHERE collectionId = :collectionId AND memoryId = :memoryId")
    suspend fun removeMember(collectionId: String, memoryId: String)

    @Query("SELECT * FROM collections ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<CollectionEntity>>

    @Query("SELECT memoryId FROM collection_members WHERE collectionId = :collectionId")
    fun observeMemberIds(collectionId: String): Flow<List<String>>
}
