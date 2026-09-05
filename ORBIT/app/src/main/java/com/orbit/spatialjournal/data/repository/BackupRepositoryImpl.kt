package com.orbit.spatialjournal.data.repository

import android.content.Context
import android.net.Uri
import com.orbit.spatialjournal.backup.BackupManager
import com.orbit.spatialjournal.backup.RestoreManager
import com.orbit.spatialjournal.domain.repository.BackupManifest
import com.orbit.spatialjournal.domain.repository.BackupRepository
import com.orbit.spatialjournal.domain.repository.MemoryRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val memoryRepository: MemoryRepository,
    private val backupManager: BackupManager,
    private val restoreManager: RestoreManager
) : BackupRepository {

    override suspend fun createBackup(destinationUri: String, encrypt: Boolean, passphrase: String?): BackupManifest {
        val memories = memoryRepository.observeAll().first()
        val payloadBytes = backupManager.buildPayloadBytes(memories)
        val checksum = backupManager.checksum(payloadBytes)

        val tempFile = File(context.cacheDir, "orbit_backup_${System.currentTimeMillis()}.orbitbackup")
        backupManager.writeBackupFile(payloadBytes, tempFile, encrypt)

        context.contentResolver.openOutputStream(Uri.parse(destinationUri))?.use { out ->
            tempFile.inputStream().use { it.copyTo(out) }
        }
        tempFile.delete()

        return BackupManifest(
            createdAt = System.currentTimeMillis(),
            appVersion = "1.0.0",
            memoryCount = memories.size,
            checksumSha256 = checksum,
            encrypted = encrypt
        )
    }

    override suspend fun restoreBackup(sourceUri: String, passphrase: String?): Result<Unit> = runCatching {
        val tempFile = File(context.cacheDir, "orbit_restore_${System.currentTimeMillis()}.orbitbackup")
        context.contentResolver.openInputStream(Uri.parse(sourceUri))?.use { input ->
            tempFile.outputStream().use { input.copyTo(it) }
        }
        // Try unencrypted first, then encrypted, since the manifest format is not exposed pre-parse.
        val bytes = runCatching { backupManager.readBackupFile(tempFile, encrypted = false) }
            .getOrElse { backupManager.readBackupFile(tempFile, encrypted = true) }
        val payload = backupManager.parsePayload(bytes)
        restoreManager.restore(payload)
        tempFile.delete()
    }
}
