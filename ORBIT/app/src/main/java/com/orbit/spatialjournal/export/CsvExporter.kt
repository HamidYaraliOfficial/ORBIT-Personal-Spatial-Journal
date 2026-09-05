package com.orbit.spatialjournal.export

import com.orbit.spatialjournal.core.model.Memory
import javax.inject.Inject

class CsvExporter @Inject constructor() {
    fun export(memories: List<Memory>): String {
        val header = "id,title,type,timestamp,latitude,longitude,placeName,city,country,tags"
        val rows = memories.joinToString("\n") { m ->
            listOf(
                m.id, csvSafe(m.title), m.type.name, m.timestamp.toString(),
                m.location?.latitude?.toString() ?: "", m.location?.longitude?.toString() ?: "",
                csvSafe(m.placeName ?: ""), csvSafe(m.city ?: ""), csvSafe(m.country ?: ""),
                csvSafe(m.tags.joinToString(";"))
            ).joinToString(",")
        }
        return "$header\n$rows"
    }

    private fun csvSafe(value: String): String =
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"${value.replace("\"", "\"\"")}\""
        } else value
}
