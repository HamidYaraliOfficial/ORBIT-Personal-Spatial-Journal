package com.orbit.spatialjournal.domain.usecase

import com.orbit.spatialjournal.core.model.DuplicateCandidate
import com.orbit.spatialjournal.data.local.dao.AttachmentDao
import com.orbit.spatialjournal.media.DuplicateDetectionEngine
import javax.inject.Inject

class DetectDuplicatesUseCase @Inject constructor(
    private val attachmentDao: AttachmentDao,
    private val engine: DuplicateDetectionEngine
) {
    suspend operator fun invoke(): List<DuplicateCandidate> {
        val attachments = attachmentDao.getAllWithPerceptualHash()
        return engine.findDuplicates(attachments)
    }
}
