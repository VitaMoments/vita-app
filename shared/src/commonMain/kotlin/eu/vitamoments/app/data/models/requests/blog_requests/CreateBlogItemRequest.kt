package eu.vitamoments.app.data.models.requests.blog_requests

import eu.vitamoments.app.data.models.domain.common.RichTextDocument
import eu.vitamoments.app.data.models.enums.BlogStatus
import eu.vitamoments.app.data.models.enums.PrivacyStatus
import kotlinx.serialization.Serializable

@Serializable
data class CreateBlogItemRequest(
    val title: String,
    val subtitle: String? = null,
    val coverImageUrl: String? = null,
    val coverImageAlt: String? = null,
    val document: RichTextDocument,
    val privacy: PrivacyStatus = PrivacyStatus.FRIENDS_ONLY,
    val status: BlogStatus = BlogStatus.DRAFT
)
