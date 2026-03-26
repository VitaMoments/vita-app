package eu.vitamoments.app.data.models.domain.media

import eu.vitamoments.app.data.models.enums.MediaPurposeType
import eu.vitamoments.app.data.models.enums.MediaReferenceType
import eu.vitamoments.app.data.models.enums.PrivacyStatus
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
sealed interface MediaAsset {
    val uuid: Uuid
    val url: String
    val contentType: String
    val sizeBytes: Long
    val purpose: MediaPurposeType
    val privacy: PrivacyStatus
    val referenceType: MediaReferenceType
    val referenceId: Uuid
}