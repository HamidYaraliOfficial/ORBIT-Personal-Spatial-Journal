package com.orbit.spatialjournal.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.spatialjournal.core.model.Memory
import com.orbit.spatialjournal.data.datastore.SettingsDataStore
import com.orbit.spatialjournal.domain.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val results: List<Memory> = emptyList(),
    val isSearching: Boolean = false
)

/** Powers both the structured Search Engine and the Natural Language Memory Search box —
 * the same text field drives both, since NL parsing degrades gracefully to a plain keyword search. */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state.asStateFlow()

    fun setQuery(query: String) { _state.value = _state.value.copy(query = query) }

    fun search() {
        val query = _state.value.query
        if (query.isBlank()) { _state.value = _state.value.copy(results = emptyList()); return }
        viewModelScope.launch {
            _state.value = _state.value.copy(isSearching = true)
            val language = settingsDataStore.language.first()
            val results = searchRepository.searchNaturalLanguage(query, language.tag)
            _state.value = _state.value.copy(results = results, isSearching = false)
        }
    }
}
