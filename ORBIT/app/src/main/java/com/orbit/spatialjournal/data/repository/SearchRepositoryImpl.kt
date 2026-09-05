package com.orbit.spatialjournal.data.repository

import com.orbit.spatialjournal.ai.NaturalLanguageQueryParser
import com.orbit.spatialjournal.core.model.Memory
import com.orbit.spatialjournal.core.model.SearchFilters
import com.orbit.spatialjournal.domain.repository.MemoryRepository
import com.orbit.spatialjournal.domain.repository.SearchRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepositoryImpl @Inject constructor(
    private val memoryRepository: MemoryRepository,
    private val nlParser: NaturalLanguageQueryParser
) : SearchRepository {

    override suspend fun search(filters: SearchFilters): List<Memory> = memoryRepository.search(filters)

    override suspend fun searchNaturalLanguage(rawQuery: String, languageTag: String): List<Memory> {
        val filters = nlParser.parse(rawQuery, languageTag)
        return memoryRepository.search(filters)
    }
}
