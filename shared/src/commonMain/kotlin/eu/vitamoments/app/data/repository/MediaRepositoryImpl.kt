package eu.vitamoments.app.data.repository

import eu.vitamoments.app.data.models.domain.media.MediaAssetContext
import eu.vitamoments.app.data.models.domain.media.MediaCreateInput
import eu.vitamoments.app.data.models.enums.MediaPurposeType
import eu.vitamoments.app.data.models.enums.MediaReferenceType
import kotlin.uuid.Uuid

class MediaRepositoryImpl : MediaRepository {
    override suspend fun create(input: MediaCreateInput): RepositoryResult<MediaAssetContext> {
        TODO("Not yet implemented")
    }

    override suspend fun findById(id: Uuid): RepositoryResult<MediaAssetContext> {
        TODO("Not yet implemented")
    }

    override suspend fun findAllByReference(
        referenceId: Uuid,
        referenceType: MediaReferenceType
    ): RepositoryResult<List<MediaAssetContext>> {
        TODO("Not yet implemented")
    }

    override suspend fun findProfileImage(userId: Uuid): RepositoryResult<MediaAssetContext?> {
        TODO("Not yet implemented")
    }

    override suspend fun findAllByReferenceAndPurpose(
        referenceId: Uuid,
        referenceType: MediaReferenceType,
        purpose: MediaPurposeType
    ): RepositoryResult<List<MediaAssetContext>> {
        TODO("Not yet implemented")
    }

    override suspend fun softDelete(
        id: Uuid,
        deletedBy: Uuid
    ): RepositoryResult<Boolean> {
        TODO("Not yet implemented")
    }
}