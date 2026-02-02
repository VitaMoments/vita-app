package eu.vitamoments.app.data.models.domain.feed

import eu.vitamoments.app.data.models.enums.PrivacyStatus
import eu.vitamoments.app.data.models.domain.common.RichTextDocument
import eu.vitamoments.app.data.models.domain.user.User
import eu.vitamoments.app.data.serializer.InstantSerializer
import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
sealed interface FeedItem {
    val uuid: Uuid
    val author: User
    val content: RichTextDocument
    val privacy: PrivacyStatus
    @Serializable(with = InstantSerializer::class) val createdAt: Instant
    @Serializable(with = InstantSerializer::class) val updatedAt: Instant
    @Serializable(with = InstantSerializer::class) val deletedAt: Instant?
}