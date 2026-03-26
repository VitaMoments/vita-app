package eu.vitamoments.app.data.models.domain.media

import eu.vitamoments.app.data.models.enums.MediaPurposeType
import eu.vitamoments.app.data.models.enums.MediaReferenceType
import eu.vitamoments.app.data.models.enums.PrivacyStatus
import kotlin.uuid.Uuid


data class MediaCreateInput(
    val uuid: Uuid,
    val referenceId: Uuid,
    val referenceType: MediaReferenceType,
    val purpose: MediaPurposeType,
    val privacy: PrivacyStatus,
    val originalFileName: String?,
    val storedFileName: String,
    val objectKey: String,
    val contentType: String,
    val sizeBytes: Long,
    val width: Int? = null,
    val height: Int? = null,
    val createdBy: Uuid?
)
