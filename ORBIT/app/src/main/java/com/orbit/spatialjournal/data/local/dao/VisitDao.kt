package com.orbit.spatialjournal.data.local.dao

import androidx.room.*
import com.orbit.spatialjournal.core.model.VisitStatus
import com.orbit.spatialjournal.data.local.entity.VisitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VisitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(visit: VisitEntity)

    @Query("SELECT * FROM visits WHERE status = :status ORDER BY arrivalAt DESC")
    fun observeByStatus(status: VisitStatus): Flow<List<VisitEntity>>

    @Query("SELECT * FROM visits ORDER BY arrivalAt DESC")
    fun observeAll(): Flow<List<VisitEntity>>

    @Query("UPDATE visits SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: VisitStatus)

    @Query("SELECT * FROM visits WHERE placeId = :placeId ORDER BY arrivalAt DESC")
    suspend fun getForPlace(placeId: String): List<VisitEntity>
}
