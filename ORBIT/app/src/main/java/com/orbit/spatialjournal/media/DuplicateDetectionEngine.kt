package com.orbit.spatialjournal.media

import com.orbit.spatialjournal.core.model.DuplicateCandidate
import com.orbit.spatialjournal.core.util.Constants
import com.orbit.spatialjournal.core.util.HashUtils
import com.orbit.spatialjournal.data.local.entity.AttachmentEntity
import javax.inject.Inject

/**
 * Finds likely-duplicate photos: exact matches via SHA-256, near-duplicates (re-saved,
 * recompressed, lightly cropped) via perceptual-hash Hamming distance. Never deletes
 * anything itself — it only produces [DuplicateCandidate]s for the user to review and
 * confirm a merge/delete for, per the "removal is always user-confirmed" requirement.
 */
class DuplicateDetectionEngine @Inject constructor() {

    fun findDuplicates(attachments: List<AttachmentEntity>): List<DuplicateCandidate> {
        val results = mutableListOf<DuplicateCandidate>()

        // Exact duplicates
        attachments.filter { it.sha256 != null }
            .groupBy { it.sha256 }
            .filterValues { it.size > 1 }
            .forEach { (_, group) ->
                for (i in group.indices) for (j in i + 1 until group.size) {
                    results += DuplicateCandidate(group[i].memoryId, group[j].memoryId, 1.0f, "identical_file")
                }
            }

        // Near-duplicates via perceptual hash
        val withHash = attachments.filter { it.averageHash != null }
        for (i in withHash.indices) {
            for (j in i + 1 until withHash.size) {
                val a = withHash[i]; val b = withHash[j]
                if (a.memoryId == b.memoryId) continue
                val distance = HashUtils.hammingDistance(a.averageHash!!, b.averageHash!!)
                if (distance <= Constants.DUPLICATE_HAMMING_THRESHOLD) {
                    val similarity = 1f - (distance / 64f)
                    results += DuplicateCandidate(a.memoryId, b.memoryId, similarity, "visually_similar")
                }
            }
        }

        return results.distinctBy { setOf(it.memoryIdA, it.memoryIdB) }
    }
}
