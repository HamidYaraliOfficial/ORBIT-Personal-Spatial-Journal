package com.orbit.spatialjournal.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.spatialjournal.core.model.Memory
import com.orbit.spatialjournal.core.model.MapStyleOption
import com.orbit.spatialjournal.data.datastore.SettingsDataStore
import com.orbit.spatialjournal.domain.repository.MemoryRepository
import com.orbit.spatialjournal.location.OrbitLocationManager
import com.orbit.spatialjournal.map.MapCluster
import com.orbit.spatialjournal.map.MarkerClusterManager
import com.orbit.spatialjournal.map.RegionSummary
import com.orbit.spatialjournal.map.RegionSummaryBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MapUiState(
    val allMemories: List<Memory> = emptyList(),
    val clusters: List<MapCluster> = emptyList(),
    val zoom: Float = 4f,
    val selectedCluster: MapCluster? = null,
    val regionSummary: RegionSummary? = null,
    val mapStyle: MapStyleOption = MapStyleOption.STANDARD,
    val typeFilter: Set<com.orbit.spatialjournal.core.model.MemoryType> = emptySet(),
    val dateFilterFrom: Long? = null,
    val dateFilterTo: Long? = null
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val memoryRepository: MemoryRepository,
    private val settingsDataStore: SettingsDataStore,
    val locationManager: OrbitLocationManager
) : ViewModel() {

    private val _zoom = MutableStateFlow(4f)
    private val _typeFilter = MutableStateFlow<Set<com.orbit.spatialjournal.core.model.MemoryType>>(emptySet())
    private val _selectedCluster = MutableStateFlow<MapCluster?>(null)

    val uiState: StateFlow<MapUiState> = combine(
        memoryRepository.observeAll(), _zoom, _typeFilter, settingsDataStore.mapStyle, _selectedCluster
    ) { flows ->
        @Suppress("UNCHECKED_CAST")
        val all = flows[0] as List<Memory>
        val zoom = flows[1] as Float
        val typeFilter = flows[2] as Set<com.orbit.spatialjournal.core.model.MemoryType>
        val style = flows[3] as MapStyleOption
        val selected = flows[4] as MapCluster?

        val filtered = if (typeFilter.isEmpty()) all else all.filter { it.type in typeFilter }
        val clusters = MarkerClusterManager.cluster(filtered, zoom)
        val regionSummary = if (zoom < 10f) RegionSummaryBuilder.build(filtered) else null

        MapUiState(
            allMemories = filtered, clusters = clusters, zoom = zoom,
            selectedCluster = selected, regionSummary = regionSummary,
            mapStyle = style, typeFilter = typeFilter
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MapUiState())

    fun onZoomChanged(zoom: Float) { _zoom.value = zoom }
    fun onClusterClick(cluster: MapCluster) { _selectedCluster.value = cluster }
    fun dismissSelection() { _selectedCluster.value = null }
    fun toggleTypeFilter(type: com.orbit.spatialjournal.core.model.MemoryType) {
        _typeFilter.value = if (type in _typeFilter.value) _typeFilter.value - type else _typeFilter.value + type
    }
    fun setMapStyle(style: MapStyleOption) {
        viewModelScope.launch { settingsDataStore.setMapStyle(style) }
    }
}
