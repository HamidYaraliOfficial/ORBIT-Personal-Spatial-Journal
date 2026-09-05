package com.orbit.spatialjournal.data.local.dao

import androidx.room.*
import com.orbit.spatialjournal.core.model.TripStatus
import com.orbit.spatialjournal.data.local.entity.TripEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(trip: TripEntity)

    @Delete
    suspend fun delete(trip: TripEntity)

    @Query("SELECT * FROM trips WHERE id = :id")
    suspend fun getById(id: String): TripEntity?

    @Query("SELECT * FROM trips WHERE id = :id")
    fun observeById(id: String): Flow<TripEntity?>

    @Query("SELECT * FROM trips ORDER BY startDate DESC")
    fun observeAll(): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE status = :status ORDER BY startDate DESC")
    fun observeByStatus(status: TripStatus): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE isAutoSuggested = 1 AND status = 'SUGGESTED'")
    fun observeSuggested(): Flow<List<TripEntity>>
}
