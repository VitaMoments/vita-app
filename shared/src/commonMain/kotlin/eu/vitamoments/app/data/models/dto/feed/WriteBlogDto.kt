package eu.vitamoments.app.data.models.dto.feed

import eu.vitamoments.app.data.enums.BlogStatus
import eu.vitamoments.app.data.enums.PrivacyStatus
import eu.vitamoments.app.data.models.domain.richtext.RichTextDocument
import kotlinx.serialization.Serializable

@Serializable
data class WriteBlogDto(
    val title: String,
    val subtitle: String? = null,
    val coverImageUrl: String? = null,
    val coverImageAlt: String? = null,

    val content: RichTextDocument,

    val privacy: PrivacyStatus = PrivacyStatus.FRIENDS_ONLY,
    val status: BlogStatus = BlogStatus.DRAFT
)
