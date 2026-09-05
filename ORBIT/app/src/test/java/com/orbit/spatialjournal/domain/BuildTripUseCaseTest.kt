package com.orbit.spatialjournal.domain

import com.google.common.truth.Truth.assertThat
import com.orbit.spatialjournal.core.model.*
import com.orbit.spatialjournal.domain.repository.MemoryRepository
import com.orbit.spatialjournal.domain.repository.TripRepository
import com.orbit.spatialjournal.domain.usecase.BuildTripUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Test

/** Minimal hand-written fakes — no mocking framework needed for these narrow interfaces. */
private class FakeMemoryRepository(private val memories: List<Memory>) : MemoryRepository {
    override suspend fun saveMemory(memory: Memory) {}
    override suspend fun deleteMemory(id: String) {}
    override suspend fun getMemory(id: String): Memory? = memories.find { it.id == id }
    override fun observeMemory(id: String): Flow<Memory?> = flowOf(getMemory(id))
    override fun observeAll(): Flow<List<Memory>> = flowOf(memories)
    override fun observeRecent(limit: Int): Flow<List<Memory>> = flowOf(memories.take(limit))
    override fun observeByType(type: MemoryType): Flow<List<Memory>> = flowOf(memories.filter { it.type == type })
    override fun observeByTrip(tripId: String): Flow<List<Memory>> = flowOf(memories.filter { it.tripId == tripId })
    override fun observeByPlace(placeId: String): Flow<List<Memory>> = flowOf(emptyList())
    override fun observeInDateRange(fromEpoch: Long, toEpoch: Long): Flow<List<Memory>> = flowOf(memories)
    override suspend fun getInDateRange(fromEpoch: Long, toEpoch: Long): List<Memory> = memories
    override fun observeInMapBounds(minLat: Double, maxLat: Double, minLon: Double, maxLon: Double): Flow<List<Memory>> = flowOf(memories)
    override fun observeFavorites(): Flow<List<Memory>> = flowOf(memories.filter { it.isFavorite })
    override fun observeTotalCount(): Flow<Int> = flowOf(memories.size)
    override suspend fun setFavorite(id: String, favorite: Boolean) {}
    override suspend fun setArchived(id: String, archived: Boolean) {}
    override suspend fun search(filters: SearchFilters): List<Memory> = memories
}

private class FakeTripRepository : TripRepository {
    val saved = mutableListOf<Trip>()
    override suspend fun saveTrip(trip: Trip) { saved.add(trip) }
    override suspend fun deleteTrip(id: String) {}
    override suspend fun getTrip(id: String): Trip? = saved.find { it.id == id }
    override fun observeTrip(id: String): Flow<Trip?> = flowOf(getTrip(id))
    override fun observeAll(): Flow<List<Trip>> = flowOf(saved)
    override fun observeByStatus(status: TripStatus): Flow<List<Trip>> = flowOf(saved.filter { it.status == status })
    override fun observeSuggested(): Flow<List<Trip>> = flowOf(saved.filter { it.status == TripStatus.SUGGESTED })
    override suspend fun acceptSuggestedTrip(id: String) {}
    override suspend fun rejectSuggestedTrip(id: String) {}
}

class BuildTripUseCaseTest {

    private fun memory(id: String, city: String, country: String, daysOffset: Long) = Memory(
        id = id, title = id, type = MemoryType.PHOTO,
        timestamp = 1_700_000_000_000L + daysOffset * 86_400_000L,
        location = GeoPoint(35.0, 51.0), city = city, country = country
    )

    @Test
    fun `suggests a trip when city changes across a large gap`() = runTest {
        val memories = listOf(
            memory("m1", "Tehran", "Iran", 0),
            memory("m2", "Tehran", "Iran", 1),
            memory("m3", "Paris", "France", 10), // 9-day gap + different country -> new trip
            memory("m4", "Paris", "France", 11)
        )
        val useCase = BuildTripUseCase(FakeMemoryRepository(memories), FakeTripRepository())
        val suggestions = useCase.suggestTrips(memories)
        assertThat(suggestions).isNotEmpty()
    }

    @Test
    fun `does not suggest a trip for memories in the same city`() = runTest {
        val memories = listOf(
            memory("m1", "Tehran", "Iran", 0),
            memory("m2", "Tehran", "Iran", 1),
            memory("m3", "Tehran", "Iran", 2)
        )
        val useCase = BuildTripUseCase(FakeMemoryRepository(memories), FakeTripRepository())
        val suggestions = useCase.suggestTrips(memories)
        assertThat(suggestions).isEmpty()
    }

    @Test
    fun `manual trip creation links all given memory ids`() = runTest {
        val memories = listOf(memory("m1", "Tehran", "Iran", 0), memory("m2", "Tehran", "Iran", 1))
        val tripRepo = FakeTripRepository()
        val useCase = BuildTripUseCase(FakeMemoryRepository(memories), tripRepo)
        val trip = useCase.createManualTrip("Weekend trip", listOf("m1", "m2"))
        assertThat(trip.memoryIds).containsExactly("m1", "m2")
        assertThat(tripRepo.saved).contains(trip)
    }
}
