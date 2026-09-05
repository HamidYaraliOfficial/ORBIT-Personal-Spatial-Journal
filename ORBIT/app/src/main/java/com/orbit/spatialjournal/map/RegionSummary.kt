package com.orbit.spatialjournal.map

import com.orbit.spatialjournal.core.model.Memory
import com.orbit.spatialjournal.core.model.MemoryType

data class RegionSummary(
    val memoryCount: Int,
    val photoCount: Int,
    val voiceCount: Int,
    val firstDate: Long?,
    val lastDate: Long?,
    val topTags: List<String>
)

/** Computes the summary card shown when the Map is zoomed into a city/country region. */
object RegionSummaryBuilder {

    fun build(memories: List<Memory>): RegionSummary {
        if (memories.isEmpty()) {
            return RegionSummary(0, 0, 0, null, null, emptyList())
        }
        val sorted = memories.sortedBy { it.timestamp }
        val topTags = memories.flatMap { it.tags }
            .groupingBy { it }.eachCount()
            .entries.sortedByDescending { it.value }
            .take(5).map { it.key }

        return RegionSummary(
            memoryCount = memories.size,
            photoCount = memories.count { it.type == MemoryType.PHOTO },
            voiceCount = memories.count { it.type == MemoryType.VOICE },
            firstDate = sorted.first().timestamp,
            lastDate = sorted.last().timestamp,
            topTags = topTags
        )
    }
}
