package eu.vitamoments.app.data.tables

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import org.jetbrains.exposed.v1.json.jsonb

object TimelineItemsTable: UUIDTable("timeline_items") {
    val feedItemId = reference("feed_item_id", FeedItemsTable, onDelete = ReferenceOption.CASCADE).uniqueIndex()
    val content = jsonb(name = "content",  jsonConfig = Json, kSerializer = JsonElement.serializer())
}