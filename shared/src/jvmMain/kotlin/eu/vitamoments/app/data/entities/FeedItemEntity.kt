package eu.vitamoments.app.data.entities

import eu.vitamoments.app.data.models.enums.FeedCategory
import eu.vitamoments.app.data.tables.FeedItemCategoriesTable
import eu.vitamoments.app.data.tables.FeedItemsTable
import eu.vitamoments.app.data.tables.TimelineItemsTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.selectAll
import java.util.UUID

class FeedItemEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<FeedItemEntity>(FeedItemsTable)

    var type by FeedItemsTable.type
    var author by UserEntity referencedOn FeedItemsTable.author
    var privacy by FeedItemsTable.privacy
    var createdAt by FeedItemsTable.createdAt
    var updatedAt by FeedItemsTable.updatedAt
    var deletedAt by FeedItemsTable.deletedAt

    // ✅ categories via join table
    var categories: List<FeedCategory>
        get() = FeedItemCategoriesTable
            .selectAll()
            .where { FeedItemCategoriesTable.feedItemId eq this.id }
            .map { it[FeedItemCategoriesTable.category] }

        set(value) {
            // zorg dat we consistent opslaan (distinct + max 3 als je die rule wilt afdwingen)
            val normalized = value.distinct()

            FeedItemCategoriesTable.deleteWhere { feedItemId eq this@FeedItemEntity.id }

            if (normalized.isNotEmpty()) {
                FeedItemCategoriesTable.batchInsert(normalized) { cat ->
                    this[FeedItemCategoriesTable.feedItemId] = this@FeedItemEntity.id
                    this[FeedItemCategoriesTable.category] = cat
                }
            }
        }

    // Optional helper relations
    val timeline by TimelineItemEntity optionalBackReferencedOn TimelineItemsTable.feedItemId
}