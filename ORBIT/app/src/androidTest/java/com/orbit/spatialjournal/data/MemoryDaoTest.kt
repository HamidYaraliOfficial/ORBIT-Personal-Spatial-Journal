package com.orbit.spatialjournal.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.orbit.spatialjournal.core.model.MemoryType
import com.orbit.spatialjournal.core.model.MemorySource
import com.orbit.spatialjournal.data.local.AppDatabase
import com.orbit.spatialjournal.data.local.dao.MemoryDao
import com.orbit.spatialjournal.data.local.entity.MemoryEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented Room test — runs against a real in-memory SQLite database on-device/emulator,
 * covering the DAO layer, FTS search sync, and the map-bounds query used by the Map screen.
 */
@RunWith(AndroidJUnit4::class)
class MemoryDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: MemoryDao

    private fun sample(id: String, title: String, lat: Double? = 35.0, lon: Double? = 51.0) = MemoryEntity(
        id = id, title = title, description = "desc for $title", type = MemoryType.NOTE,
        timestamp = System.currentTimeMillis(), startTime = null, endTime = null,
        latitude = lat, longitude = lon, accuracyMeters = null, placeName = null, country = null,
        city = null, address = null, tagsJson = "[]", attachmentsJson = "[]",
        source = MemorySource.MANUAL_ENTRY, mood = null, relatedMemoryIdsJson = "[]",
        notes = null, metadataJson = "{}", version = 1, tripId = null, placeId = null,
        createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis()
    )

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
            .allowMainThreadQueries().build()
        dao = db.memoryDao()
    }

    @After
    fun tearDown() { db.close() }

    @Test
    fun insertAndRetrieveById() = runBlocking {
        dao.upsertAndIndex(sample("m1", "Coffee with friends"))
        val loaded = dao.getById("m1")
        assertThat(loaded?.title).isEqualTo("Coffee with friends")
    }

    @Test
    fun fullTextSearchFindsMatchingTitle() = runBlocking {
        dao.upsertAndIndex(sample("m1", "Sunset hike in the mountains"))
        dao.upsertAndIndex(sample("m2", "Grocery shopping"))
        val results = dao.searchFts("mountains*")
        assertThat(results).hasSize(1)
        assertThat(results.first().id).isEqualTo("m1")
    }

    @Test
    fun mapBoundsQueryOnlyReturnsPointsInsideBox() = runBlocking {
        dao.upsertAndIndex(sample("inside", "Inside box", lat = 35.5, lon = 51.5))
        dao.upsertAndIndex(sample("outside", "Outside box", lat = 60.0, lon = 100.0))
        val inBounds = dao.observeInBounds(34.0, 36.0, 50.0, 52.0).first()
        assertThat(inBounds.map { it.id }).containsExactly("inside")
    }

    @Test
    fun deletingMemoryRemovesItFromResults() = runBlocking {
        dao.upsertAndIndex(sample("toDelete", "Temporary"))
        dao.deleteById("toDelete")
        assertThat(dao.getById("toDelete")).isNull()
    }
}
