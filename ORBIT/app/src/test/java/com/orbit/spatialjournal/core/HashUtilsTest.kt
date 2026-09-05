package com.orbit.spatialjournal.core

import com.google.common.truth.Truth.assertThat
import com.orbit.spatialjournal.core.util.HashUtils
import org.junit.Test

class HashUtilsTest {

    @Test
    fun `sha256 is deterministic for identical bytes`() {
        val bytes = "orbit test payload".toByteArray()
        assertThat(HashUtils.sha256(bytes)).isEqualTo(HashUtils.sha256(bytes))
    }

    @Test
    fun `sha256 differs for different bytes`() {
        assertThat(HashUtils.sha256("a".toByteArray())).isNotEqualTo(HashUtils.sha256("b".toByteArray()))
    }

    @Test
    fun `hamming distance of identical hashes is zero`() {
        assertThat(HashUtils.hammingDistance(0b1010101L, 0b1010101L)).isEqualTo(0)
    }

    @Test
    fun `hamming distance counts differing bits`() {
        assertThat(HashUtils.hammingDistance(0b0000L, 0b1111L)).isEqualTo(4)
    }
}
