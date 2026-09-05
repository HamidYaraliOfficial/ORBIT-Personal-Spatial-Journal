package com.orbit.spatialjournal.ui.screens.trips

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.spatialjournal.core.model.GeneratedStory
import com.orbit.spatialjournal.core.model.Memory
import com.orbit.spatialjournal.core.model.Trip
import com.orbit.spatialjournal.domain.repository.MemoryRepository
import com.orbit.spatialjournal.domain.repository.TripRepository
import com.orbit.spatialjournal.domain.usecase.GenerateStoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripsViewModel @Inject constructor(
    tripRepository: TripRepository,
    private val tripRepo: TripRepository
) : ViewModel() {
    val trips: StateFlow<List<Trip>> = tripRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun acceptSuggestion(id: String) = viewModelScope.launch { tripRepo.acceptSuggestedTrip(id) }
    fun rejectSuggestion(id: String) = viewModelScope.launch { tripRepo.rejectSuggestedTrip(id) }
}

data class TripDetailUiState(
    val trip: Trip? = null,
    val memories: List<Memory> = emptyList(),
    val story: GeneratedStory? = null
)

@HiltViewModel
class TripDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val tripRepository: TripRepository,
    private val memoryRepository: MemoryRepository,
    private val generateStory: GenerateStoryUseCase
) : ViewModel() {

    private val tripId: String = checkNotNull(savedStateHandle["tripId"])
    private val _state = MutableStateFlow(TripDetailUiState())
    val state: StateFlow<TripDetailUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            tripRepository.observeTrip(tripId).collect { trip ->
                val memories = trip?.memoryIds?.mapNotNull { memoryRepository.getMemory(it) } ?: emptyList()
                _state.value = _state.value.copy(trip = trip, memories = memories)
            }
        }
    }

    fun generateAiSummary() {
        viewModelScope.launch {
            _state.value = _state.value.copy(story = generateStory.forTrip(tripId))
        }
    }
}
