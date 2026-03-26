package eu.vitamoments.app.data.entities

import eu.vitamoments.app.data.tables.MediaAssetsTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.util.UUID

class MediaAssetEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object Companion: UUIDEntityClass<MediaAssetEntity>(MediaAssetsTable)

    var referenceId by MediaAssetsTable.referenceId
    var referenceType by MediaAssetsTable.referenceType

    var purpose by MediaAssetsTable.purpose
    var privacy by MediaAssetsTable.privacy
    var originalFileName by MediaAssetsTable.originalFileName
    var storedFileName by MediaAssetsTable.storedFileName
    var objectKey by MediaAssetsTable.objectKey
    var contentType by MediaAssetsTable.contentType
    var sizeBytes by MediaAssetsTable.sizeBytes
    var width by MediaAssetsTable.width
    var height by MediaAssetsTable.height

    var createdAt by MediaAssetsTable.createdAt
    var updatedAt by MediaAssetsTable.updatedAt
    var deletedAt by MediaAssetsTable.deletedAt
    var createdBy by MediaAssetsTable.createdBy
    var updatedBy by MediaAssetsTable.updatedBy
    var deletedBy by MediaAssetsTable.deletedBy
}