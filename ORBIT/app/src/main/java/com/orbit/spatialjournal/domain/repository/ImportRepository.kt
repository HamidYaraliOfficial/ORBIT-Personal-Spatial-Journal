package com.orbit.spatialjournal.domain.repository

import kotlinx.coroutines.flow.Flow

data class ImportProgress(
    val totalItems: Int,
    val processedItems: Int,
    val currentItemLabel: String,
    val isPaused: Boolean = false,
    val isComplete: Boolean = false,
    val failedItems: Int = 0
)

interface ImportRepository {
    fun observeProgress(): Flow<ImportProgress>
    suspend fun startImportFromGallery(uris: List<String>)
    suspend fun pause()
    suspend fun resume()
    suspend fun retryFailed()
}
