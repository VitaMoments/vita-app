package eu.vitamoments.app.data.models.domain.feed

import eu.vitamoments.app.data.models.enums.BlogCategory
import eu.vitamoments.app.data.models.enums.BlogStatus
import eu.vitamoments.app.data.models.enums.PrivacyStatus
import eu.vitamoments.app.data.models.domain.common.RichTextDocument
import eu.vitamoments.app.data.models.domain.user.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
@SerialName("BLOGITEM")
data class BlogItem(
    override val uuid: Uuid,
    override val author: User,

    val title: String,
    val subtitle: String? = null,

    val slug: String,
    val coverImageUrl: String? = null,
    val coverImageAlt: String? = null,

    override val content: RichTextDocument,
    override val privacy: PrivacyStatus = PrivacyStatus.FRIENDS_ONLY,

    val categories: List<BlogCategory> = emptyList(),
    val status: BlogStatus = BlogStatus.DRAFT,
    val publishedAt: Instant? = null,

    override val createdAt: Instant,
    override val updatedAt: Instant,
    override val deletedAt: Instant? = null
) : FeedItem
