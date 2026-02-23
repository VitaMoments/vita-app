package eu.vitamoments.app.data.tables

import eu.vitamoments.app.data.models.enums.BlogStatus
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import org.jetbrains.exposed.v1.datetime.datetime
import org.jetbrains.exposed.v1.json.jsonb

object BlogItemsTable: UUIDTable("blog_items") {
    val feedItemId = reference("feed_item_id", FeedItemsTable, onDelete = ReferenceOption.CASCADE).uniqueIndex()
    val title = varchar("title", 200)
    val subtitle = varchar("subtitle", 255).nullable()
    val slug = varchar("slug", 250).uniqueIndex()
    val coverImageUrl = varchar("cover_image_url", 500).nullable()
    val coverImageAlt = varchar("cover_image_alt", 200).nullable()
    val status = enumerationByName<BlogStatus>("blog_status", 15).clientDefault { BlogStatus.DRAFT }
    val publishedAt = datetime("published_at").nullable()

    val content = jsonb("content", Json, JsonElement.serializer())
}