package eu.vitamoments.app.data.models.domain.media

import eu.vitamoments.app.data.models.enums.MediaPurposeType
import eu.vitamoments.app.data.models.enums.MediaReferenceType
import eu.vitamoments.app.data.models.enums.PrivacyStatus
import eu.vitamoments.app.data.serializer.InstantSerializer
import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
data class MediaAssetContext(
    override val uuid : Uuid,
    override val referenceId: Uuid,
    override val referenceType: MediaReferenceType,
    override val purpose: MediaPurposeType,
    override val privacy: PrivacyStatus,
    override val contentType: String,
    override val sizeBytes: Long,
    override val url: String,

    val originalFileName: String? = null,
    val storedFileName: String,
    val objectKey: String,
    val width: Int? = null,
    val height: Int? = null,
    @Serializable(with = InstantSerializer::class) val createdAt: Instant,
    @Serializable(with = InstantSerializer::class) val updatedAt: Instant,
    @Serializable(with = InstantSerializer::class) val deletedAt: Instant? = null,
    val createdBy: Uuid,
    val updatedBy: Uuid? = null,
    val deletedBy: Uuid? = null
) : MediaAsset
