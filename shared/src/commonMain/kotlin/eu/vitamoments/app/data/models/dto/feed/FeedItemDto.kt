package eu.vitamoments.app.data.models.dto.feed

import eu.vitamoments.app.data.enums.BlogCategory
import eu.vitamoments.app.data.enums.BlogStatus
import eu.vitamoments.app.data.enums.PrivacyStatus
import eu.vitamoments.app.data.models.domain.feed.BlogItem
import eu.vitamoments.app.data.models.domain.richtext.RichTextDocument
import eu.vitamoments.app.data.models.dto.user.UserDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
sealed interface FeedItemDto {
    val uuid: Uuid
    val author: UserDto
    val content: RichTextDocument
    val privacy: PrivacyStatus
    val createdAt: Long
    val updatedAt: Long
    val deletedAt: Long?
}

@Serializable
@SerialName("BLOGITEM")
data class BlogItemDto(
    override val uuid: Uuid,
    override val author: UserDto,

    val title: String,
    val subtitle: String? = null,
    val slug: String,

    val coverImageUrl: String? = null,
    val coverImageAlt: String? = null,

    override val content: RichTextDocument,
    override val privacy: PrivacyStatus,

    val categories: List<BlogCategory>,
    val status: BlogStatus,
    val publishedAt: Long? = null,

    override val createdAt: Long,
    override val updatedAt: Long,
    override val deletedAt: Long? = null,
    val html: String,
) : FeedItemDto

@Serializable
@SerialName("TIMELINEITEM")
data class TimelineItemDto(
    override val uuid: Uuid,
    override val createdAt: Long,
    override val updatedAt: Long,
    override val deletedAt: Long?,
    override val content: RichTextDocument,
    override val author: UserDto,
    override val privacy: PrivacyStatus,

    val plainText: String,
    val html: String,
) : FeedItemDto