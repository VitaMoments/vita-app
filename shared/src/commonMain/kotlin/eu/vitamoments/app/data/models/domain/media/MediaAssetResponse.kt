package eu.vitamoments.app.data.models.domain.media

import eu.vitamoments.app.data.models.enums.MediaPurposeType
import eu.vitamoments.app.data.models.enums.MediaReferenceType
import eu.vitamoments.app.data.models.enums.PrivacyStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
@SerialName("RESPONSE")
data class MediaAssetResponse(
    override val uuid : Uuid,
    override val referenceId: Uuid,
    override val referenceType: MediaReferenceType,
    override val purpose: MediaPurposeType,
    override val privacy: PrivacyStatus,
    override val contentType: String,
    override val sizeBytes: Long,
    override val url: String,
): MediaAsset