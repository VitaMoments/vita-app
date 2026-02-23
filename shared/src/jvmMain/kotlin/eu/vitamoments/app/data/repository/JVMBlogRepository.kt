package eu.vitamoments.app.data.repository

import eu.vitamoments.app.data.entities.UserEntity
import eu.vitamoments.app.data.factory.FeedItemFactory
import eu.vitamoments.app.data.mapper.entity.toDomain
import eu.vitamoments.app.data.models.domain.common.RichTextDocument
import eu.vitamoments.app.data.models.domain.feed.BlogItem
import eu.vitamoments.app.data.models.enums.BlogStatus
import eu.vitamoments.app.data.models.enums.FeedCategory
import eu.vitamoments.app.data.models.enums.PrivacyStatus
import eu.vitamoments.app.data.models.helpers.extension_functions.isBlankOrNullRichText
import eu.vitamoments.app.data.tables.UsersTable
import eu.vitamoments.app.dbHelpers.dbQuery
import org.jetbrains.exposed.v1.core.eq
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

class JVMBlogRepository : BlogRepository {
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
    ): RepositoryResult<BlogItem> = dbQuery {
        val viewerUuid = userId.toJavaUuid()
        val errors = mutableListOf<RepositoryError.FieldError>()

        val userEntity = UserEntity
            .find { UsersTable.id eq viewerUuid }
            .firstOrNull()

        if (userEntity == null) {
            errors += RepositoryError.FieldError(field = "author", "this userId is not registered as User")
        }

        if (document.content.isBlankOrNullRichText()) {
            errors += RepositoryError.FieldError(field = "content", "Content of this blog cannot be empty")
        }

        if (errors.isNotEmpty()) {
            return@dbQuery RepositoryResult.Error(RepositoryError.BadRequest(
                errors = errors
            ))
        }

        val entity = FeedItemFactory.newBlogItem(
            author = userEntity!!,
            title = title,
            subtitle = subtitle,
            content = document.content!!,
            coverImageUrl = coverImageUrl,
            coverImageAlt = coverImageAlt,
            categories = categories,
            status = status,
            privacy = privacy,
            publishedAt = null
        )

        entity.refresh(true)

        RepositoryResult.Success(entity.toDomain(userId))
    }
}