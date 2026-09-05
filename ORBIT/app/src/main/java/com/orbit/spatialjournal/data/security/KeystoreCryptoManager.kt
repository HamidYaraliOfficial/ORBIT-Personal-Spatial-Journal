package com.orbit.spatialjournal.data.security

import android.content.Context
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Wraps Android Keystore + Jetpack Security so sensitive files (backups, journal exports)
 * are encrypted at rest with a key that never leaves the device's secure hardware.
 */
@Singleton
class KeystoreCryptoManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    fun writeEncrypted(file: File, bytes: ByteArray) {
        if (file.exists()) file.delete()
        val encryptedFile = EncryptedFile.Builder(
            context, file, masterKey, EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()
        encryptedFile.openFileOutput().use { it.write(bytes) }
    }

    fun readEncrypted(file: File): ByteArray {
        val encryptedFile = EncryptedFile.Builder(
            context, file, masterKey, EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()
        return encryptedFile.openFileInput().use { it.readBytes() }
    }

    fun encryptedPrefsName(): String = "orbit_encrypted_prefs"
    fun masterKeyAlias(): MasterKey = masterKey
}
