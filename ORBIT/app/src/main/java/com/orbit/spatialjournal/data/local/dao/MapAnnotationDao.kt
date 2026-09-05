package com.orbit.spatialjournal.data.local.dao

import androidx.room.*
import com.orbit.spatialjournal.data.local.entity.MapAnnotationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MapAnnotationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(annotation: MapAnnotationEntity)

    @Delete
    suspend fun delete(annotation: MapAnnotationEntity)

    @Query("SELECT * FROM map_annotations ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<MapAnnotationEntity>>
}
