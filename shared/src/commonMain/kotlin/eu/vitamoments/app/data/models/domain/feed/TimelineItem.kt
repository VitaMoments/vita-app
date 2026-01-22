package eu.vitamoments.app.data.models.domain.feed

import eu.vitamoments.app.data.enums.PrivacyStatus
import eu.vitamoments.app.data.models.domain.richtext.RichTextDocument
import eu.vitamoments.app.data.models.domain.user.User
import kotlin.time.Instant
import kotlin.uuid.Uuid

data class TimelineItem(
    override val uuid: Uuid,
    override val author: User,
    override val content: RichTextDocument,
    override val privacy: PrivacyStatus = PrivacyStatus.FRIENDS_ONLY,
    override val createdAt: Instant,
    override val updatedAt: Instant,
    override val deletedAt: Instant? = null
) : FeedItem