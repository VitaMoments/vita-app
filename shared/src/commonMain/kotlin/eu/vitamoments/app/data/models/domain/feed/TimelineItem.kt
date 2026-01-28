package eu.vitamoments.app.data.models.domain.feed

import eu.vitamoments.app.data.models.enums.PrivacyStatus
import eu.vitamoments.app.data.models.domain.common.RichTextDocument
import eu.vitamoments.app.data.models.domain.user.User
import eu.vitamoments.app.data.serializer.InstantSerializer
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
    @Serializable(with = InstantSerializer::class) override val createdAt: Instant,
    @Serializable(with = InstantSerializer::class) override val updatedAt: Instant,
    @Serializable(with = InstantSerializer::class) override val deletedAt: Instant? = null
) : FeedItem