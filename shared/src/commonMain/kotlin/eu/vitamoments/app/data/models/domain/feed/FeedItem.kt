package eu.vitamoments.app.data.models.domain.feed

import eu.vitamoments.app.data.models.enums.PrivacyStatus
import eu.vitamoments.app.data.models.domain.common.RichTextDocument
import eu.vitamoments.app.data.models.domain.user.User
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
sealed interface FeedItem {
    val uuid: Uuid
    val author: User
    val content: RichTextDocument
    val privacy: PrivacyStatus
    @Contextual val createdAt: Instant
    @Contextual val updatedAt: Instant
    @Contextual val deletedAt: Instant?
}