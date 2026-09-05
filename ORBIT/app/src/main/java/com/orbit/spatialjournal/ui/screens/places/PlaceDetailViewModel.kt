package com.orbit.spatialjournal.ui.screens.places

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.spatialjournal.core.model.Memory
import com.orbit.spatialjournal.core.model.OpeningHoursEntry
import com.orbit.spatialjournal.core.model.OpeningStatus
import com.orbit.spatialjournal.core.model.Place
import com.orbit.spatialjournal.domain.repository.MemoryRepository
import com.orbit.spatialjournal.domain.repository.PlaceRepository
import com.orbit.spatialjournal.domain.usecase.ComputeOpeningStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlaceDetailUiState(
    val place: Place? = null,
    val memories: List<Memory> = emptyList(),
    val openingStatus: OpeningStatus? = null
)

@HiltViewModel
class PlaceDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val placeRepository: PlaceRepository,
    private val memoryRepository: MemoryRepository,
    private val computeOpeningStatus: ComputeOpeningStatusUseCase
) : ViewModel() {

    private val placeId: String = checkNotNull(savedStateHandle["placeId"])
    private val _state = MutableStateFlow(PlaceDetailUiState())
    val state: StateFlow<PlaceDetailUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            placeRepository.observePlace(placeId).collect { place ->
                val memories = memoryRepository.observeByPlace(placeId).first()
                val status = computeOpeningStatus(placeId)
                _state.value = PlaceDetailUiState(place = place, memories = memories, openingStatus = status)
            }
        }
    }

    fun updateOpeningHours(hours: List<OpeningHoursEntry>) {
        viewModelScope.launch {
            placeRepository.updateOpeningHours(placeId, hours)
            _state.value = _state.value.copy(openingStatus = computeOpeningStatus(placeId))
        }
    }

    fun toggleFavorite() {
        val place = _state.value.place ?: return
        viewModelScope.launch { placeRepository.setFavorite(place.id, !place.isFavorite) }
    }
}
