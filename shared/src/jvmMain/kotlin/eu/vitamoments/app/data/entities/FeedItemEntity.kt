package eu.vitamoments.app.data.entities

import eu.vitamoments.app.data.tables.BlogItemsTable
import eu.vitamoments.app.data.tables.FeedItemsTable
import eu.vitamoments.app.data.tables.TimelineItemsTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.util.UUID

class FeedItemEntity(id: EntityID<UUID>) : UUIDEntity(id) {

    companion object : UUIDEntityClass<FeedItemEntity>(FeedItemsTable)

    var type by FeedItemsTable.type
    var author by UserEntity referencedOn FeedItemsTable.author
    var privacy by FeedItemsTable.privacy
    var createdAt by FeedItemsTable.createdAt
    var updatedAt by FeedItemsTable.updatedAt
    var deletedAt by FeedItemsTable.deletedAt

    // Optional helper relations
    val blog by BlogItemEntity optionalBackReferencedOn BlogItemsTable.feedItemId
    val timeline by TimelineItemEntity optionalBackReferencedOn TimelineItemsTable.feedItemId
}