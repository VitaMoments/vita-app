package eu.vitamoments.app.data.repository

import eu.vitamoments.app.data.entities.MediaAssetEntity
import eu.vitamoments.app.data.mapper.entity.toMediaAsset
import eu.vitamoments.app.data.mapper.extension_functions.nowUtc
import eu.vitamoments.app.data.models.domain.media.MediaAssetContext
import eu.vitamoments.app.data.models.domain.media.MediaCreateInput
import eu.vitamoments.app.data.models.enums.MediaPurposeType
import eu.vitamoments.app.data.models.enums.MediaReferenceType
import eu.vitamoments.app.data.tables.MediaAssetsTable
import eu.vitamoments.app.dbHelpers.dbQuery
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.isNull
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

class JVMMediaRepository : MediaRepository {
    override suspend fun create(input: MediaCreateInput): RepositoryResult<MediaAssetContext> = dbQuery {
        val entity = MediaAssetEntity.new(
            id= input.uuid.toJavaUuid()
        ) {
            this.referenceId = input.referenceId
            this.referenceType = input.referenceType
            this.purpose = input.purpose
            this.privacy = input.privacy
            this.originalFileName = input.originalFileName
            this.storedFileName = input.storedFileName
            this.objectKey = input.objectKey
            this.contentType = input.contentType
            this.sizeBytes = input.sizeBytes
            this.width = input.width
            this.height = input.height
            this.createdBy = input.createdBy
            this.updatedBy = input.createdBy
        }

        RepositoryResult.Success(entity.toMediaAsset())
    }

    override suspend fun findById(id: Uuid): RepositoryResult<MediaAssetContext> = dbQuery {
        val entity = MediaAssetEntity.findById(id.toJavaUuid())
            ?.takeIf { it.deletedAt == null }
            ?: return@dbQuery RepositoryResult.Error(
                RepositoryError.NotFound(message = "No media found with id $id")
            )

        RepositoryResult.Success(entity.toMediaAsset())
    }

    override suspend fun findAllByReference(
        referenceId: Uuid,
        referenceType: MediaReferenceType
    ): RepositoryResult<List<MediaAssetContext>> = dbQuery {
        val list = MediaAssetEntity.find {
            (MediaAssetsTable.referenceId eq referenceId) and
                    (MediaAssetsTable.referenceType eq referenceType) and
                    MediaAssetsTable.deletedAt.isNull()
        }
            .map { it.toMediaAsset() }
            .toList()

        RepositoryResult.Success(list)
    }

    override suspend fun findProfileImage(userId: Uuid): RepositoryResult<MediaAssetContext?> = dbQuery {
        val entity = MediaAssetEntity.find {
            (MediaAssetsTable.referenceId eq userId) and
                    (MediaAssetsTable.referenceType eq MediaReferenceType.USER) and
                    (MediaAssetsTable.purpose eq MediaPurposeType.PROFILE) and
                    MediaAssetsTable.deletedAt.isNull()
        }
            .orderBy(MediaAssetsTable.createdAt to SortOrder.DESC)
            .firstOrNull()
        RepositoryResult.Success(entity?.toMediaAsset())
    }

    override suspend fun findAllByReferenceAndPurpose(
        referenceId: Uuid,
        referenceType: MediaReferenceType,
        purpose: MediaPurposeType
    ): RepositoryResult<List<MediaAssetContext>> = dbQuery {
        val list = MediaAssetEntity.find {
            (MediaAssetsTable.referenceId eq referenceId) and
                    (MediaAssetsTable.referenceType eq referenceType) and
                    (MediaAssetsTable.purpose eq purpose) and
                    MediaAssetsTable.deletedAt.isNull()
        }
            .map { it.toMediaAsset() }
            .toList()

        RepositoryResult.Success(list)
    }

    override suspend fun softDelete(
        id: Uuid,
        deletedBy: Uuid
    ): RepositoryResult<Boolean> = dbQuery {
        val entity = MediaAssetEntity.findById(id.toJavaUuid())
            ?.takeIf { it.deletedAt == null }
            ?: return@dbQuery RepositoryResult.Error(
                RepositoryError.NotFound(message = "No media found with id $id")
            )

        val now = LocalDateTime.nowUtc()
        entity.deletedAt = now
        entity.updatedAt = now
        entity.updatedBy = deletedBy
        entity.deletedBy = deletedBy

        RepositoryResult.Success(true)
    }

}