package com.orbit.spatialjournal.core.util

import android.graphics.Bitmap
import java.security.MessageDigest

/**
 * Backs the Duplicate Detection Engine.
 *  - [sha256] catches byte-identical duplicates (e.g. the same photo imported twice).
 *  - [averageHash] is a classic perceptual hash: shrink to 8x8, grayscale, compare against
 *    the mean, one bit per pixel. Cheap enough to run on-device for every imported photo,
 *    and robust to re-encoding / minor recompression.
 */
object HashUtils {

    fun sha256(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun averageHash(bitmap: Bitmap): Long {
        val size = 8
        val shrunk = Bitmap.createScaledBitmap(bitmap, size, size, true)
        val gray = IntArray(size * size)
        var sum = 0L
        for (y in 0 until size) {
            for (x in 0 until size) {
                val pixel = shrunk.getPixel(x, y)
                val r = (pixel shr 16) and 0xFF
                val g = (pixel shr 8) and 0xFF
                val b = pixel and 0xFF
                val luminance = (r * 299 + g * 587 + b * 114) / 1000
                gray[y * size + x] = luminance
                sum += luminance
            }
        }
        val avg = sum / (size * size)
        var hash = 0L
        for (i in gray.indices) {
            if (gray[i] >= avg) hash = hash or (1L shl i)
        }
        return hash
    }

    /** Hamming distance between two average-hashes; 0 = identical, higher = more different. */
    fun hammingDistance(a: Long, b: Long): Int = java.lang.Long.bitCount(a xor b)
}
