package com.orbit.spatialjournal.ui.screens.memories

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.spatialjournal.ai.AIAssistantProvider
import com.orbit.spatialjournal.core.model.Memory
import com.orbit.spatialjournal.domain.repository.MemoryRepository
import com.orbit.spatialjournal.domain.repository.RelationshipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MemoryDetailUiState(
    val memory: Memory? = null,
    val relatedMemories: List<Memory> = emptyList()
)

@HiltViewModel
class MemoryDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val memoryRepository: MemoryRepository,
    private val relationshipRepository: RelationshipRepository,
    private val assistant: AIAssistantProvider
) : ViewModel() {

    private val memoryId: String = checkNotNull(savedStateHandle["memoryId"])

    private val _state = MutableStateFlow(MemoryDetailUiState())
    val state: StateFlow<MemoryDetailUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            memoryRepository.observeMemory(memoryId).collect { memory ->
                _state.value = _state.value.copy(memory = memory)
                if (memory != null) loadRelated(memory)
            }
        }
    }

    private suspend fun loadRelated(memory: Memory) {
        val all = memoryRepository.observeAll().first()
        val related = assistant.findRelated(memory, all)
        _state.value = _state.value.copy(relatedMemories = related)
    }

    fun toggleFavorite() {
        val memory = _state.value.memory ?: return
        viewModelScope.launch { memoryRepository.setFavorite(memory.id, !memory.isFavorite) }
    }

    fun archive() {
        val memory = _state.value.memory ?: return
        viewModelScope.launch { memoryRepository.setArchived(memory.id, true) }
    }

    fun addTag(tag: String) {
        val memory = _state.value.memory ?: return
        if (tag.isBlank() || tag in memory.tags) return
        viewModelScope.launch { memoryRepository.saveMemory(memory.copy(tags = memory.tags + tag.trim())) }
    }
}
