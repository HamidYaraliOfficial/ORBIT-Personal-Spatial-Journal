package com.orbit.spatialjournal.export

import com.orbit.spatialjournal.core.model.Memory
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class JsonExporter @Inject constructor() {
    private val json = Json { prettyPrint = true; encodeDefaults = true }
    fun export(memories: List<Memory>): String = json.encodeToString(memories)
}
