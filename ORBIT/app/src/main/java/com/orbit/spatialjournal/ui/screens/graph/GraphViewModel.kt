package com.orbit.spatialjournal.ui.screens.graph

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.spatialjournal.core.model.GraphEdge
import com.orbit.spatialjournal.core.model.GraphNode
import com.orbit.spatialjournal.domain.repository.MemoryRepository
import com.orbit.spatialjournal.domain.repository.RelationshipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GraphUiState(val nodes: List<GraphNode> = emptyList(), val edges: List<GraphEdge> = emptyList())

@HiltViewModel
class GraphViewModel @Inject constructor(
    private val memoryRepository: MemoryRepository,
    private val relationshipRepository: RelationshipRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GraphUiState())
    val state: StateFlow<GraphUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val memories = memoryRepository.observeAll().first()
            val relationships = relationshipRepository.getAll()

            val nodes = memories.map { GraphNode(id = it.id, label = it.title, kind = "memory") }
            val edges = relationships.map { GraphEdge(it.fromMemoryId, it.toMemoryId, it.type.name) }
            _state.value = GraphUiState(nodes = nodes, edges = edges)
        }
    }
}
