package eu.vitamoments.app.data.repository

import eu.vitamoments.app.data.models.domain.common.RichTextDocument
import eu.vitamoments.app.data.models.domain.feed.BlogItem
import eu.vitamoments.app.data.models.enums.BlogStatus
import eu.vitamoments.app.data.models.enums.FeedCategory
import eu.vitamoments.app.data.models.enums.PrivacyStatus
import kotlin.uuid.Uuid

interface BlogRepository {
    suspend fun create(
        userId: Uuid,
        title: String,
        subtitle: String? = null,
        categories: List<FeedCategory> = emptyList(),
        coverImageUrl: String? = null,
        coverImageAlt: String? = null,
        document: RichTextDocument,
        privacy: PrivacyStatus = PrivacyStatus.FRIENDS_ONLY,
        status: BlogStatus = BlogStatus.DRAFT
    ) : RepositoryResult<BlogItem>
}