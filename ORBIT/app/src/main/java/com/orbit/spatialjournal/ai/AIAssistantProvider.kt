package com.orbit.spatialjournal.ai

import com.orbit.spatialjournal.core.model.GeneratedStory
import com.orbit.spatialjournal.core.model.Memory
import com.orbit.spatialjournal.core.model.RecapData

/**
 * Everything the "AI Spatial Assistant" can do, expressed as a provider interface so the
 * default on-device implementation ([LocalRuleBasedAssistant]) can later be swapped for a
 * cloud LLM — but only if a user explicitly opts in from the Privacy Center, since AI must
 * only ever see memories the user has allowed it to use.
 *
 * The default implementation shipped with ORBIT never leaves the device: it builds recaps,
 * trip summaries and stories purely from local aggregation over the user's own Room
 * database, so every sentence it produces is traceable back to real memories
 * (see [RecapData.highlightMemoryIds] / [GeneratedStory.sections]).
 */
interface AIAssistantProvider {
    fun buildDayRecap(memories: List<Memory>, dayLabel: String, fromEpoch: Long, toEpoch: Long): RecapData
    fun buildRangeRecap(memories: List<Memory>, periodLabel: String, fromEpoch: Long, toEpoch: Long): RecapData
    fun buildTripSummary(memories: List<Memory>, tripName: String): GeneratedStory
    fun buildStory(memories: List<Memory>, title: String): GeneratedStory
    fun findRelated(target: Memory, candidates: List<Memory>, maxResults: Int = 8): List<Memory>
}
