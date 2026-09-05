package com.orbit.spatialjournal.data.repository

import com.orbit.spatialjournal.data.local.dao.ReminderDao
import com.orbit.spatialjournal.data.local.entity.ReminderEntity
import com.orbit.spatialjournal.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepositoryImpl @Inject constructor(
    private val dao: ReminderDao
) : ReminderRepository {
    override suspend fun save(reminder: ReminderEntity) = dao.upsert(reminder)
    override suspend fun delete(reminder: ReminderEntity) = dao.delete(reminder)
    override fun observeActive(): Flow<List<ReminderEntity>> = dao.observeActive()
    override suspend fun getPendingGeofenceReminders(): List<ReminderEntity> = dao.getPendingGeofenceReminders()
    override suspend fun getDueTimeReminders(nowEpoch: Long): List<ReminderEntity> = dao.getDueTimeReminders(nowEpoch)
    override suspend fun markFired(id: String) = dao.markFired(id)
}
