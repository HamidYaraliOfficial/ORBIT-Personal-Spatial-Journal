package com.orbit.spatialjournal.data.local.dao

import androidx.room.*
import com.orbit.spatialjournal.data.local.entity.PlaceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(place: PlaceEntity)

    @Delete
    suspend fun delete(place: PlaceEntity)

    @Query("SELECT * FROM places WHERE id = :id")
    suspend fun getById(id: String): PlaceEntity?

    @Query("SELECT * FROM places WHERE id = :id")
    fun observeById(id: String): Flow<PlaceEntity?>

    @Query("SELECT * FROM places ORDER BY lastVisitAt DESC")
    fun observeAll(): Flow<List<PlaceEntity>>

    @Query("SELECT * FROM places WHERE isFavorite = 1")
    fun observeFavorites(): Flow<List<PlaceEntity>>

    @Query("SELECT * FROM places WHERE country = :country")
    fun observeByCountry(country: String): Flow<List<PlaceEntity>>

    @Query("SELECT * FROM places WHERE city = :city")
    fun observeByCity(city: String): Flow<List<PlaceEntity>>

    @Query("SELECT DISTINCT country FROM places WHERE country IS NOT NULL ORDER BY country ASC")
    fun observeDistinctCountries(): Flow<List<String>>

    @Query(
        """SELECT * FROM places WHERE latitude BETWEEN :minLat AND :maxLat
           AND longitude BETWEEN :minLon AND :maxLon"""
    )
    suspend fun getInBounds(minLat: Double, maxLat: Double, minLon: Double, maxLon: Double): List<PlaceEntity>

    @Query("SELECT * FROM places ORDER BY name ASC LIMIT :limit")
    suspend fun searchByNamePrefix(limit: Int = 50): List<PlaceEntity>

    @Query("SELECT * FROM places WHERE name LIKE '%' || :query || '%' OR city LIKE '%' || :query || '%'")
    suspend fun search(query: String): List<PlaceEntity>
}
