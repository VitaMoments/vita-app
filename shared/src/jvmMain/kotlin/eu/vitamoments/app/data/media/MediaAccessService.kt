package eu.vitamoments.app.data.media

import eu.vitamoments.app.data.models.domain.media.MediaAssetContext
import eu.vitamoments.app.data.models.enums.MediaReferenceType
import eu.vitamoments.app.data.models.enums.PrivacyStatus
import kotlin.uuid.Uuid

class MediaAccessService {

    suspend fun canView(
        media: MediaAssetContext,
        requesterUserId: Uuid?
    ): Boolean {
        return when (media.privacy) {
            PrivacyStatus.OPEN -> true

            PrivacyStatus.PRIVATE -> {
                requesterUserId != null && requesterUserId == media.createdBy
            }

            PrivacyStatus.FRIENDS_ONLY -> {
                requesterUserId != null && when (media.referenceType) {
                    MediaReferenceType.USER -> requesterUserId == media.referenceId || requesterUserId == media.createdBy
                    else -> requesterUserId == media.createdBy
                }
            }
        }
    }

    suspend fun canDelete(
        media: MediaAssetContext,
        requesterUserId: Uuid?
    ): Boolean {
        return requesterUserId != null && requesterUserId == media.createdBy
    }

    suspend fun canUpload(
        referenceId: Uuid,
        referenceType: MediaReferenceType,
        requesterUserId: Uuid?
    ): Boolean {
        if (requesterUserId == null) return false

        return when (referenceType) {
            MediaReferenceType.USER -> requesterUserId == referenceId
            else -> true
        }
    }
}