package com.orbit.spatialjournal.data.local.dao

import androidx.room.*
import com.orbit.spatialjournal.data.local.entity.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfAbsent(tag: TagEntity)

    @Query("UPDATE tags SET usageCount = usageCount + 1 WHERE name = :name")
    suspend fun incrementUsage(name: String)

    @Transaction
    suspend fun touch(name: String) {
        insertIfAbsent(TagEntity(name = name, usageCount = 0))
        incrementUsage(name)
    }

    @Query("SELECT * FROM tags ORDER BY usageCount DESC LIMIT :limit")
    fun observeTopTags(limit: Int = 30): Flow<List<TagEntity>>

    @Query("SELECT * FROM tags ORDER BY name ASC")
    fun observeAll(): Flow<List<TagEntity>>
}
