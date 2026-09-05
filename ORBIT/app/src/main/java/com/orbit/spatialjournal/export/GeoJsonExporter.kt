package com.orbit.spatialjournal.export

import com.orbit.spatialjournal.core.model.Memory
import javax.inject.Inject

/** Exports located memories as a GeoJSON FeatureCollection, importable into most map tools. */
class GeoJsonExporter @Inject constructor() {
    fun export(memories: List<Memory>): String {
        val features = memories.filter { it.location != null }.joinToString(",\n") { m ->
            """{
              |  "type": "Feature",
              |  "geometry": { "type": "Point", "coordinates": [${m.location!!.longitude}, ${m.location.latitude}] },
              |  "properties": {
              |    "id": "${m.id}", "title": ${jsonString(m.title)}, "type": "${m.type}",
              |    "timestamp": ${m.timestamp}, "placeName": ${jsonString(m.placeName ?: "")}
              |  }
              |}""".trimMargin()
        }
        return """{ "type": "FeatureCollection", "features": [ $features ] }"""
    }

    private fun jsonString(s: String) = "\"${s.replace("\"", "\\\"")}\""
}
