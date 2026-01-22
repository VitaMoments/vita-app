package eu.vitamoments.app.data.models.domain.feed

import eu.vitamoments.app.data.enums.BlogCategory
import eu.vitamoments.app.data.enums.BlogStatus
import eu.vitamoments.app.data.enums.PrivacyStatus
import eu.vitamoments.app.data.models.domain.richtext.RichTextDocument
import eu.vitamoments.app.data.models.domain.user.User
import kotlin.time.Instant
import kotlin.uuid.Uuid

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
