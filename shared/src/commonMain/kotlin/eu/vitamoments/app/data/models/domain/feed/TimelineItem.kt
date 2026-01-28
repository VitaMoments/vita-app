package eu.vitamoments.app.data.models.domain.feed

import eu.vitamoments.app.data.models.enums.PrivacyStatus
import eu.vitamoments.app.data.models.domain.common.RichTextDocument
import eu.vitamoments.app.data.models.domain.user.User
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
@SerialName("TIMELINEITEM")
data class TimelineItem(
    override val uuid: Uuid,
    override val author: User,
    override val content: RichTextDocument,
    override val privacy: PrivacyStatus = PrivacyStatus.FRIENDS_ONLY,
    @Contextual override val createdAt: Instant,
    @Contextual override val updatedAt: Instant,
    @Contextual override val deletedAt: Instant? = null
) : FeedItem