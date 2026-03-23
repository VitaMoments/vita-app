package eu.vitamoments.app.data.repository

import eu.vitamoments.app.data.models.domain.media.MediaAssetContext
import eu.vitamoments.app.data.models.domain.media.MediaCreateInput
import eu.vitamoments.app.data.models.enums.MediaPurposeType
import eu.vitamoments.app.data.models.enums.MediaReferenceType
import kotlin.uuid.Uuid

interface MediaRepository {
    suspend fun create(input: MediaCreateInput) : RepositoryResult<MediaAssetContext>
    suspend fun findById(id: Uuid) : RepositoryResult<MediaAssetContext>
    suspend fun findAllByReference(
        referenceId: Uuid,
        referenceType: MediaReferenceType
    ): RepositoryResult<List<MediaAssetContext>>
    suspend fun findProfileImage(
        userId: Uuid
    ): RepositoryResult<MediaAssetContext?>
    suspend fun findAllByReferenceAndPurpose(
        referenceId: Uuid,
        referenceType: MediaReferenceType,
        purpose: MediaPurposeType
    ): RepositoryResult<List<MediaAssetContext>>
    suspend fun softDelete(id: Uuid, deletedBy: Uuid): RepositoryResult<Boolean>
}