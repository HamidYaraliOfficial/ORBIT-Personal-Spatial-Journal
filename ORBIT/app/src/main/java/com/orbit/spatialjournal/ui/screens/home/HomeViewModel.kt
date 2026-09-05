package com.orbit.spatialjournal.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.spatialjournal.core.model.Memory
import com.orbit.spatialjournal.core.model.RecapData
import com.orbit.spatialjournal.core.model.TimelineGranularity
import com.orbit.spatialjournal.core.model.Trip
import com.orbit.spatialjournal.domain.repository.MemoryRepository
import com.orbit.spatialjournal.domain.repository.TripRepository
import com.orbit.spatialjournal.domain.usecase.GenerateRecapUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val recentMemories: List<Memory> = emptyList(),
    val totalMemoryCount: Int = 0,
    val suggestedTrips: List<Trip> = emptyList(),
    val todayRecap: RecapData? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    memoryRepository: MemoryRepository,
    tripRepository: TripRepository,
    private val generateRecap: GenerateRecapUseCase
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        memoryRepository.observeRecent(8),
        memoryRepository.observeTotalCount(),
        tripRepository.observeSuggested()
    ) { recent, count, suggested ->
        HomeUiState(recentMemories = recent, totalMemoryCount = count, suggestedTrips = suggested, isLoading = false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    private val _recap = kotlinx.coroutines.flow.MutableStateFlow<RecapData?>(null)
    val recap: StateFlow<RecapData?> = _recap.asStateFlow()

    init {
        viewModelScope.launch {
            _recap.value = generateRecap(TimelineGranularity.DAY)
        }
    }
}
