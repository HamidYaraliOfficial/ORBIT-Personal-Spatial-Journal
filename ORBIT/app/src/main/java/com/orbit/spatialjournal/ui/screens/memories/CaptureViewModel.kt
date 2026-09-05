package com.orbit.spatialjournal.ui.screens.memories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.spatialjournal.core.model.*
import com.orbit.spatialjournal.core.util.IdGenerator
import com.orbit.spatialjournal.domain.repository.MemoryRepository
import com.orbit.spatialjournal.domain.usecase.BuildRelationshipsUseCase
import com.orbit.spatialjournal.domain.usecase.CreateMemoryUseCase
import com.orbit.spatialjournal.location.OrbitLocationManager
import com.orbit.spatialjournal.voice.AudioRecorderManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CaptureUiState(
    val type: MemoryType = MemoryType.NOTE,
    val title: String = "",
    val description: String = "",
    val tags: List<String> = emptyList(),
    val location: GeoPoint? = null,
    val isRecordingVoice: Boolean = false,
    val isSaving: Boolean = false,
    val savedMemoryId: String? = null
)

@HiltViewModel
class CaptureViewModel @Inject constructor(
    private val createMemory: CreateMemoryUseCase,
    private val buildRelationships: BuildRelationshipsUseCase,
    private val memoryRepository: MemoryRepository,
    private val locationManager: OrbitLocationManager,
    private val audioRecorder: AudioRecorderManager
) : ViewModel() {

    private val _state = MutableStateFlow(CaptureUiState())
    val state: StateFlow<CaptureUiState> = _state.asStateFlow()

    fun setType(type: MemoryType) { _state.value = _state.value.copy(type = type) }
    fun setTitle(title: String) { _state.value = _state.value.copy(title = title) }
    fun setDescription(desc: String) { _state.value = _state.value.copy(description = desc) }
    fun addTag(tag: String) {
        if (tag.isBlank()) return
        _state.value = _state.value.copy(tags = _state.value.tags + tag.trim())
    }

    /** "Smart Capture": grabs current location + timestamp automatically before saving. */
    fun captureCurrentContext() {
        viewModelScope.launch {
            val location = locationManager.getCurrentLocationOnce()
            _state.value = _state.value.copy(location = location?.point)
        }
    }

    fun startVoiceRecording() {
        audioRecorder.startRecording()
        _state.value = _state.value.copy(isRecordingVoice = true)
    }

    fun stopVoiceRecording(): String? {
        val path = audioRecorder.stopRecording()
        _state.value = _state.value.copy(isRecordingVoice = false)
        return path
    }

    fun save(attachmentUri: String? = null, mimeType: String? = null) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true)
            val s = _state.value
            val attachments = if (attachmentUri != null) listOf(
                Attachment(id = IdGenerator.newId(), uri = attachmentUri, mimeType = mimeType ?: "application/octet-stream")
            ) else emptyList()

            val draft = Memory(
                id = "", title = s.title.ifBlank { "Untitled memory" }, description = s.description.ifBlank { null },
                type = s.type, timestamp = System.currentTimeMillis(), location = s.location,
                tags = s.tags, attachments = attachments, source = MemorySource.CAPTURED_IN_APP
            )
            val saved = createMemory(draft)
            val allOthers = memoryRepository.observeAll().first().filter { it.id != saved.id }
            buildRelationships(saved, allOthers)

            _state.value = _state.value.copy(isSaving = false, savedMemoryId = saved.id)
        }
    }
}
