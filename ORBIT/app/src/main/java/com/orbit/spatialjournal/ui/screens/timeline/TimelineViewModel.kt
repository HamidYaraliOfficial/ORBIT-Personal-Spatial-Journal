package com.orbit.spatialjournal.ui.screens.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.spatialjournal.core.model.Memory
import com.orbit.spatialjournal.core.model.TimelineGranularity
import com.orbit.spatialjournal.core.util.DateTimeUtils
import com.orbit.spatialjournal.domain.repository.MemoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class TimelineUiState(
    val granularity: TimelineGranularity = TimelineGranularity.DAY,
    val anchorEpoch: Long = System.currentTimeMillis(),
    val memories: List<Memory> = emptyList()
)

@HiltViewModel
class TimelineViewModel @Inject constructor(
    private val memoryRepository: MemoryRepository
) : ViewModel() {

    private val _granularity = MutableStateFlow(TimelineGranularity.DAY)
    private val _anchor = MutableStateFlow(System.currentTimeMillis())

    val uiState: StateFlow<TimelineUiState> = _granularity.combine(_anchor) { g, a -> g to a }
        .flatMapLatest { (g, anchor) ->
            val (from, to) = rangeFor(g, anchor)
            memoryRepository.observeInDateRange(from, to).map { memories ->
                TimelineUiState(granularity = g, anchorEpoch = anchor, memories = memories)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TimelineUiState())

    fun setGranularity(g: TimelineGranularity) { _granularity.value = g }

    fun jumpTo(epoch: Long) { _anchor.value = epoch }

    fun stepBackward() { _anchor.value = shift(_anchor.value, _granularity.value, -1) }
    fun stepForward() { _anchor.value = shift(_anchor.value, _granularity.value, 1) }

    private fun rangeFor(g: TimelineGranularity, anchor: Long): Pair<Long, Long> = when (g) {
        TimelineGranularity.DAY -> DateTimeUtils.startOfDay(anchor) to DateTimeUtils.endOfDay(anchor)
        TimelineGranularity.WEEK -> DateTimeUtils.startOfWeek(anchor) to DateTimeUtils.startOfWeek(anchor) + 7L * 24 * 60 * 60 * 1000 - 1
        TimelineGranularity.MONTH -> DateTimeUtils.startOfMonth(anchor) to DateTimeUtils.startOfMonth(anchor) + 31L * 24 * 60 * 60 * 1000
        TimelineGranularity.YEAR -> DateTimeUtils.startOfYear(anchor) to DateTimeUtils.startOfYear(anchor) + 366L * 24 * 60 * 60 * 1000
        TimelineGranularity.TRAVEL -> 0L to Long.MAX_VALUE
    }

    private fun shift(anchor: Long, g: TimelineGranularity, direction: Int): Long {
        val dayMs = 24 * 60 * 60 * 1000L
        return when (g) {
            TimelineGranularity.DAY -> anchor + direction * dayMs
            TimelineGranularity.WEEK -> anchor + direction * 7 * dayMs
            TimelineGranularity.MONTH -> anchor + direction * 30 * dayMs
            TimelineGranularity.YEAR -> anchor + direction * 365 * dayMs
            TimelineGranularity.TRAVEL -> anchor
        }
    }
}
