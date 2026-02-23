package eu.vitamoments.app.data.repository

import eu.vitamoments.app.data.models.domain.common.RichTextDocument
import eu.vitamoments.app.data.models.domain.feed.BlogItem
import eu.vitamoments.app.data.models.enums.BlogStatus
import eu.vitamoments.app.data.models.enums.FeedCategory
import eu.vitamoments.app.data.models.enums.PrivacyStatus
import kotlin.uuid.Uuid

class BlogRepositoryImpl : BlogRepository {
    override suspend fun create(
        userId: Uuid,
        title: String,
        subtitle: String?,
        categories: List<FeedCategory>,
        coverImageUrl: String?,
        coverImageAlt: String?,
        document: RichTextDocument,
        privacy: PrivacyStatus,
        status: BlogStatus
    ): RepositoryResult<BlogItem> {
        TODO("Not yet implemented")
    }

}