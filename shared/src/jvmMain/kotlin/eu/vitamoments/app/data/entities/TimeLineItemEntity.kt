package eu.vitamoments.app.data.entities

import eu.vitamoments.app.data.tables.TimeLineItemsTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import java.util.UUID

class TimeLineItemEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object Companion : UUIDEntityClass<TimeLineItemEntity>(TimeLineItemsTable)
    var createdBy by UserEntity referencedOn TimeLineItemsTable.createdBy
    var createdAt by TimeLineItemsTable.createdAt
    var updatedAt by TimeLineItemsTable.updatedAt
    var deletedAt by TimeLineItemsTable.deletedAt
    var content by TimeLineItemsTable.content
}