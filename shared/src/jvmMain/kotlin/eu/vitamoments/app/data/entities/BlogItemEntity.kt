package eu.vitamoments.app.data.entities

import eu.vitamoments.app.data.tables.BlogItemsTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import java.util.UUID

class BlogItemEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object Companion : UUIDEntityClass<BlogItemEntity>(BlogItemsTable)

    var title by BlogItemsTable.title
    var subtitle by BlogItemsTable.subTitle
    var slug by BlogItemsTable.slug
    var coverImageUrl by BlogItemsTable.coverImageUrl
    var coverImageAlt by BlogItemsTable.coverImageAlt
    var privacyStatus by BlogItemsTable.privacyStatus
    var status by BlogItemsTable.status
    var publishedAt by BlogItemsTable.publishedAt
    var author by UserEntity referencedOn BlogItemsTable.authorId
    var createdAt by BlogItemsTable.createdAt
    var updatedAt by BlogItemsTable.updatedAt
    var deletedAt by BlogItemsTable.deletedAt
    var content by BlogItemsTable.content
}
