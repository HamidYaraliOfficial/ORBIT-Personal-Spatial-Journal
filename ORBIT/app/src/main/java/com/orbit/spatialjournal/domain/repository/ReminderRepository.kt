package com.orbit.spatialjournal.domain.repository

import com.orbit.spatialjournal.data.local.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    suspend fun save(reminder: ReminderEntity)
    suspend fun delete(reminder: ReminderEntity)
    fun observeActive(): Flow<List<ReminderEntity>>
    suspend fun getPendingGeofenceReminders(): List<ReminderEntity>
    suspend fun getDueTimeReminders(nowEpoch: Long): List<ReminderEntity>
    suspend fun markFired(id: String)
}
