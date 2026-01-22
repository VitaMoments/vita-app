package eu.vitamoments.app.data.tables

import eu.vitamoments.app.data.enums.BlogStatus
import eu.vitamoments.app.data.enums.PrivacyStatus
import eu.vitamoments.app.data.mapper.extension_functions.nowUtc
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.datetime.datetime
import org.jetbrains.exposed.v1.json.jsonb

object BlogItemsTable: UUIDTable("blog_items") {
    val authorId = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE)
    val title = varchar("title", 200)
    val subTitle = varchar("subtitle", 255)

    val slug = varchar("slug", 250).uniqueIndex()
    val coverImageUrl = varchar("cover_image_url", 500).nullable()
    val coverImageAlt = varchar("cover_image_alt", 200).nullable()

    val privacyStatus = enumerationByName<PrivacyStatus>("privacy_status", 15).clientDefault { PrivacyStatus.FRIENDS_ONLY }
    val status = enumerationByName<BlogStatus>("blog_status", 15).clientDefault { BlogStatus.DRAFT }
    val publishedAt = datetime("published_at").nullable()

    val createdAt = datetime("created_at").clientDefault { LocalDateTime.nowUtc() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.nowUtc() }
    val deletedAt = datetime("deleted_at").nullable()

    val content = jsonb("content", Json, JsonElement.serializer())
}