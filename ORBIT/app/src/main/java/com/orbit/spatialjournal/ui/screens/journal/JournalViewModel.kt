package com.orbit.spatialjournal.ui.screens.journal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.spatialjournal.core.util.IdGenerator
import com.orbit.spatialjournal.domain.repository.JournalBlock
import com.orbit.spatialjournal.domain.repository.JournalEntry
import com.orbit.spatialjournal.domain.repository.JournalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JournalViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val journalRepository: JournalRepository
) : ViewModel() {

    private val memoryId: String = checkNotNull(savedStateHandle["memoryId"])
    private val _entry = MutableStateFlow<JournalEntry?>(null)
    val entry: StateFlow<JournalEntry?> = _entry.asStateFlow()

    init {
        viewModelScope.launch {
            _entry.value = journalRepository.getByMemoryId(memoryId) ?: JournalEntry(
                id = IdGenerator.newId(), memoryId = memoryId, blocks = listOf(JournalBlock("text", "")),
                linkedMemoryIds = emptyList(), linkedPlaceIds = emptyList(), updatedAt = System.currentTimeMillis()
            )
        }
    }

    fun updateBlockText(index: Int, text: String) {
        val current = _entry.value ?: return
        val updatedBlocks = current.blocks.toMutableList().also {
            it[index] = it[index].copy(content = text)
        }
        _entry.value = current.copy(blocks = updatedBlocks, updatedAt = System.currentTimeMillis())
    }

    fun addBlock(type: String) {
        val current = _entry.value ?: return
        _entry.value = current.copy(blocks = current.blocks + JournalBlock(type, ""))
    }

    fun toggleChecklistItem(index: Int) {
        val current = _entry.value ?: return
        val updatedBlocks = current.blocks.toMutableList().also {
            it[index] = it[index].copy(checked = !(it[index].checked ?: false))
        }
        _entry.value = current.copy(blocks = updatedBlocks)
    }

    fun save() {
        val current = _entry.value ?: return
        viewModelScope.launch { journalRepository.save(current) }
    }
}
