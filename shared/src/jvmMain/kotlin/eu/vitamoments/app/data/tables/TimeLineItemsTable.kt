package eu.vitamoments.app.data.tables

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import eu.vitamoments.app.data.mapper.extension_functions.nowUtc
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.datetime.datetime
import org.jetbrains.exposed.v1.json.jsonb

object TimeLineItemsTable: UUIDTable("timeline_posts") {
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.nowUtc() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.nowUtc() }
    val deletedAt = datetime("deleted_at").nullable()
    val createdBy = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE)
    val content = jsonb("content", Json, JsonElement.serializer())
}