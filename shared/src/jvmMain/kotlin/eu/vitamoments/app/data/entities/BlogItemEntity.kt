package eu.vitamoments.app.data.entities

import eu.vitamoments.app.data.tables.BlogItemsTable
import eu.vitamoments.app.data.tables.TimelineItemsTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.util.UUID

class BlogItemEntity(id: EntityID<UUID>) : UUIDEntity(id) {

    companion object : UUIDEntityClass<BlogItemEntity>(BlogItemsTable)

    var feedItem by FeedItemEntity referencedOn BlogItemsTable.feedItemId
    var feedItemId by BlogItemsTable.feedItemId
    var title by BlogItemsTable.title
    var subtitle by BlogItemsTable.subtitle
    var slug by BlogItemsTable.slug
    var coverImageUrl by BlogItemsTable.coverImageUrl
    var coverImageAlt by BlogItemsTable.coverImageAlt
    var status by BlogItemsTable.status
    var publishedAt by BlogItemsTable.publishedAt
    var content by BlogItemsTable.content
}
