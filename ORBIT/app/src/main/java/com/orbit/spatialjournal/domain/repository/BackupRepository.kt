package com.orbit.spatialjournal.domain.repository

data class BackupManifest(
    val createdAt: Long,
    val appVersion: String,
    val memoryCount: Int,
    val checksumSha256: String,
    val encrypted: Boolean
)

interface BackupRepository {
    suspend fun createBackup(destinationUri: String, encrypt: Boolean, passphrase: String?): BackupManifest
    suspend fun restoreBackup(sourceUri: String, passphrase: String?): Result<Unit>
}
