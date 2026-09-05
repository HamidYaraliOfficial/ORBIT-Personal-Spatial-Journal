package com.orbit.spatialjournal.data.local.dao

import androidx.room.*
import com.orbit.spatialjournal.data.local.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(reminder: ReminderEntity)

    @Delete
    suspend fun delete(reminder: ReminderEntity)

    @Query("SELECT * FROM reminders WHERE isEnabled = 1 ORDER BY createdAt DESC")
    fun observeActive(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE isEnabled = 1 AND isFired = 0 AND geofenceLatitude IS NOT NULL")
    suspend fun getPendingGeofenceReminders(): List<ReminderEntity>

    @Query("SELECT * FROM reminders WHERE isEnabled = 1 AND isFired = 0 AND triggerAtEpochMillis IS NOT NULL AND triggerAtEpochMillis <= :nowEpoch")
    suspend fun getDueTimeReminders(nowEpoch: Long): List<ReminderEntity>

    @Query("UPDATE reminders SET isFired = 1 WHERE id = :id")
    suspend fun markFired(id: String)
}
