package eu.vitamoments.app.data.mapper.entity

import eu.vitamoments.app.data.entities.MediaAssetEntity
import eu.vitamoments.app.data.mapper.extension_functions.toInstant
import eu.vitamoments.app.data.models.domain.media.MediaAssetContext
import eu.vitamoments.app.dbHelpers.kotlinUuid

fun MediaAssetEntity.toMediaAsset(): MediaAssetContext =
    MediaAssetContext(
        uuid = this.kotlinUuid,
        referenceId = referenceId,
        referenceType = referenceType,
        purpose = purpose,
        privacy = privacy,
        originalFileName = originalFileName,
        storedFileName = storedFileName,
        objectKey = objectKey,
        contentType = contentType,
        sizeBytes = sizeBytes,
        width = width,
        height = height,
        createdAt = createdAt.toInstant(),
        updatedAt = updatedAt.toInstant(),
        deletedAt = deletedAt?.toInstant(),
        createdBy = createdBy!!,
        updatedBy = updatedBy,
        deletedBy = deletedBy,
        url = "/api/media/${this.kotlinUuid}"
    )