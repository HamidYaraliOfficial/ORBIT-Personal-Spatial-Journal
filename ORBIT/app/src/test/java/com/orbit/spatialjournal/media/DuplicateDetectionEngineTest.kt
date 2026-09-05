package com.orbit.spatialjournal.media

import com.google.common.truth.Truth.assertThat
import com.orbit.spatialjournal.data.local.entity.AttachmentEntity
import org.junit.Test

class DuplicateDetectionEngineTest {

    private val engine = DuplicateDetectionEngine()

    private fun attachment(
        id: String, memoryId: String, sha256: String? = null, avgHash: Long? = null
    ) = AttachmentEntity(
        id = id, memoryId = memoryId, uri = "content://$id", mimeType = "image/jpeg",
        sizeBytes = 1000, durationMillis = null, transcript = null, sha256 = sha256,
        averageHash = avgHash, exifCapturedAt = null, exifLatitude = null, exifLongitude = null,
        exifCameraMake = null, exifCameraModel = null, createdAt = 0L
    )

    @Test
    fun `identical sha256 across two different memories is flagged as exact duplicate`() {
        val a = attachment("a1", "mem1", sha256 = "same-hash")
        val b = attachment("a2", "mem2", sha256 = "same-hash")
        val duplicates = engine.findDuplicates(listOf(a, b))
        assertThat(duplicates).hasSize(1)
        assertThat(duplicates.first().reason).isEqualTo("identical_file")
        assertThat(duplicates.first().similarity).isEqualTo(1.0f)
    }

    @Test
    fun `close perceptual hashes are flagged as visually similar`() {
        val a = attachment("a1", "mem1", avgHash = 0b0000000000000000L)
        val b = attachment("a2", "mem2", avgHash = 0b0000000000000011L) // Hamming distance 2
        val duplicates = engine.findDuplicates(listOf(a, b))
        assertThat(duplicates).hasSize(1)
        assertThat(duplicates.first().reason).isEqualTo("visually_similar")
    }

    @Test
    fun `distant perceptual hashes are not flagged`() {
        val a = attachment("a1", "mem1", avgHash = 0x0000000000000000L)
        val b = attachment("a2", "mem2", avgHash = -0x1L) // maximal Hamming distance (64)
        val duplicates = engine.findDuplicates(listOf(a, b))
        assertThat(duplicates).isEmpty()
    }

    @Test
    fun `attachments on the same memory are never flagged against each other`() {
        val a = attachment("a1", "mem1", avgHash = 0L)
        val b = attachment("a2", "mem1", avgHash = 0L)
        assertThat(engine.findDuplicates(listOf(a, b))).isEmpty()
    }

    @Test
    fun `no attachments produces no duplicates`() {
        assertThat(engine.findDuplicates(emptyList())).isEmpty()
    }
}
