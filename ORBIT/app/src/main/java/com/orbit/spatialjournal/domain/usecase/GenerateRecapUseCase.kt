package com.orbit.spatialjournal.domain.usecase

import com.orbit.spatialjournal.ai.AIAssistantProvider
import com.orbit.spatialjournal.core.model.RecapData
import com.orbit.spatialjournal.core.model.TimelineGranularity
import com.orbit.spatialjournal.core.util.DateTimeUtils
import com.orbit.spatialjournal.domain.repository.MemoryRepository
import javax.inject.Inject

class GenerateRecapUseCase @Inject constructor(
    private val memoryRepository: MemoryRepository,
    private val assistant: AIAssistantProvider
) {
    suspend operator fun invoke(granularity: TimelineGranularity, anchorEpoch: Long = System.currentTimeMillis()): RecapData {
        val (from, to, label) = when (granularity) {
            TimelineGranularity.DAY -> Triple(
                DateTimeUtils.startOfDay(anchorEpoch), DateTimeUtils.endOfDay(anchorEpoch), "Today"
            )
            TimelineGranularity.WEEK -> Triple(
                DateTimeUtils.startOfWeek(anchorEpoch), anchorEpoch, "This week"
            )
            TimelineGranularity.MONTH -> Triple(
                DateTimeUtils.startOfMonth(anchorEpoch), anchorEpoch, "This month"
            )
            else -> Triple(DateTimeUtils.startOfYear(anchorEpoch), anchorEpoch, "This year")
        }
        val memories = memoryRepository.getInDateRange(from, to)
        return if (granularity == TimelineGranularity.DAY) {
            assistant.buildDayRecap(memories, label, from, to)
        } else {
            assistant.buildRangeRecap(memories, label, from, to)
        }
    }
}
