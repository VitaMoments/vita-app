package eu.vitamoments.app.data.models.domain.feed

import eu.vitamoments.app.data.enums.PrivacyStatus
import eu.vitamoments.app.data.models.domain.richtext.RichTextDocument
import eu.vitamoments.app.data.models.domain.user.User
import kotlin.time.Instant
import kotlin.uuid.Uuid

sealed interface FeedItem {
    val uuid: Uuid
    val author: User
    val content: RichTextDocument
    val privacy: PrivacyStatus
    val createdAt: Instant
    val updatedAt: Instant
    val deletedAt: Instant?
}