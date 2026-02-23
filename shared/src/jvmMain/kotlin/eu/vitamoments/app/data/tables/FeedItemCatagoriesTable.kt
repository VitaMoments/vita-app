package eu.vitamoments.app.data.tables

import eu.vitamoments.app.data.models.enums.FeedCategory
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table

object FeedItemCatagoriesTable: Table("feed_item_categories") {
    val feedItemId = reference(
        name = "feed_item_id",
        foreign = FeedItemsTable,
        onDelete = ReferenceOption.CASCADE
    )

    val category = enumerationByName<FeedCategory>(
        name = "category",
        length = 50
    )

    override val primaryKey = PrimaryKey(feedItemId, category)

    init {
        index(false, category)
    }
}