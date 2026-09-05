package com.orbit.spatialjournal.backup

import android.content.Context
import com.orbit.spatialjournal.core.model.Memory
import com.orbit.spatialjournal.core.util.HashUtils
import com.orbit.spatialjournal.data.security.KeystoreCryptoManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class BackupPayload(
    val version: Int,
    val createdAt: Long,
    val memories: List<Memory>
)

/**
 * Builds a single versioned backup file containing memories, notes, tags, metadata and
 * relationships. Encryption (AES-256 via Android Keystore) is optional and user-controlled;
 * [checksumSha256] lets restore verify integrity before touching the live database.
 */
@Singleton
class BackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cryptoManager: KeystoreCryptoManager
) {
    private val json = Json { prettyPrint = false; encodeDefaults = true }

    fun buildPayloadBytes(memories: List<Memory>): ByteArray {
        val payload = BackupPayload(version = 1, createdAt = System.currentTimeMillis(), memories = memories)
        return json.encodeToString(payload).toByteArray(Charsets.UTF_8)
    }

    fun checksum(bytes: ByteArray): String = HashUtils.sha256(bytes)

    fun writeBackupFile(bytes: ByteArray, destination: File, encrypt: Boolean) {
        if (encrypt) {
            cryptoManager.writeEncrypted(destination, bytes)
        } else {
            destination.writeBytes(bytes)
        }
    }

    fun readBackupFile(source: File, encrypted: Boolean): ByteArray =
        if (encrypted) cryptoManager.readEncrypted(source) else source.readBytes()

    fun parsePayload(bytes: ByteArray): BackupPayload = json.decodeFromString(bytes.toString(Charsets.UTF_8))
}
