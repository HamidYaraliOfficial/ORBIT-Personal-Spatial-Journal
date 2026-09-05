package com.orbit.spatialjournal.domain.repository

import com.orbit.spatialjournal.core.model.Memory
import com.orbit.spatialjournal.core.model.SearchFilters

interface SearchRepository {
    suspend fun search(filters: SearchFilters): List<Memory>
    /** Powers the Natural Language Memory Search box; see NaturalLanguageQueryParser. */
    suspend fun searchNaturalLanguage(rawQuery: String, languageTag: String): List<Memory>
}
