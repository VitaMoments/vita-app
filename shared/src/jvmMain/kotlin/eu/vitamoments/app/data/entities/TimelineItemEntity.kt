package eu.vitamoments.app.data.entities

import eu.vitamoments.app.data.tables.TimelineItemsTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.util.UUID

class TimelineItemEntity(id: EntityID<UUID>) : UUIDEntity(id) {

    companion object Companion : UUIDEntityClass<TimelineItemEntity>(TimelineItemsTable)

    var feedItem by FeedItemEntity referencedOn TimelineItemsTable.feedItemId

    var content by TimelineItemsTable.content
}