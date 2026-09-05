package com.orbit.spatialjournal.export

import com.orbit.spatialjournal.core.model.Memory
import javax.inject.Inject

/** Exports located memories as KML placemarks (Google Earth / most GIS tools). */
class KmlExporter @Inject constructor() {
    fun export(memories: List<Memory>): String {
        val placemarks = memories.filter { it.location != null }.joinToString("\n") { m ->
            """<Placemark>
                |<name>${escape(m.title)}</name>
                |<description>${escape(m.description ?: "")}</description>
                |<Point><coordinates>${m.location!!.longitude},${m.location.latitude}</coordinates></Point>
                |</Placemark>""".trimMargin()
        }
        return """<?xml version="1.0" encoding="UTF-8"?>
            |<kml xmlns="http://www.opengis.net/kml/2.2"><Document>
            |$placemarks
            |</Document></kml>""".trimMargin()
    }

    private fun escape(s: String) = s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
}
