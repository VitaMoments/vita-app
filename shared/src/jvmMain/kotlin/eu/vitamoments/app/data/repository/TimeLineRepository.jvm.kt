package eu.vitamoments.app.data.repository

import eu.vitamoments.app.data.entities.TimelineItemEntity
import eu.vitamoments.app.data.entities.UserEntity
import eu.vitamoments.app.data.factory.FeedItemFactory
import eu.vitamoments.app.data.models.helpers.extension_functions.isBlankRichText
import eu.vitamoments.app.data.models.enums.FriendshipStatus
import eu.vitamoments.app.data.models.enums.TimeLineFeed
import eu.vitamoments.app.data.mapper.entity.toDomain
import eu.vitamoments.app.data.mapper.extension_functions.nowUtc
import eu.vitamoments.app.data.models.domain.feed.FeedItem
import eu.vitamoments.app.data.models.domain.feed.TimelineItem
import eu.vitamoments.app.data.models.helpers.extension_functions.isBlankOrNullRichText
import eu.vitamoments.app.data.tables.FeedItemsTable
import eu.vitamoments.app.data.tables.TimelineItemsTable
import eu.vitamoments.app.data.tables.UsersTable
import eu.vitamoments.app.dbHelpers.dbQuery
import eu.vitamoments.app.dbHelpers.queries.getAcceptedFriendIds
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.JsonElement
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.neq
import org.jetbrains.exposed.v1.core.notInList
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import java.util.UUID
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

class JVMTimeLineRepository : TimeLineRepository {

    override suspend fun createPost(
        userId: Uuid,
        content: JsonElement
    ): RepositoryResult<TimelineItem> = dbQuery {
        val viewerUuid = userId.toJavaUuid()
        val errors = mutableListOf<RepositoryError.FieldError>()

        val userEntity = UserEntity
            .find { UsersTable.id eq viewerUuid }
            .firstOrNull()

        if (userEntity == null) {
            errors += RepositoryError.FieldError(field = "author", "this userId is not registered as User")
        }
        if (content.isBlankRichText()) {
            errors += RepositoryError.FieldError(field = "content", "Content cannot be empty")
        }

        if (errors.isNotEmpty()) {
            return@dbQuery RepositoryResult.Error(RepositoryError.BadRequest(
                errors = errors
            ))
        }

        val entity = FeedItemFactory.newTimelineItem(
            author= userEntity!!,
            content = content
        )

        RepositoryResult.Success(entity.toDomain(userId))
    }

    override suspend fun getTimeLine(
        userId: Uuid,
        feed: TimeLineFeed,
        limit: Int,
        offset: Long
    ): RepositoryResult<List<TimelineItem>> = dbQuery {
        val viewerUuid: UUID = userId.toJavaUuid()

        // ✅ basic hardening
        val safeLimit = limit.coerceIn(1, 100)
        val safeOffset = offset.coerceAtLeast(0L)

        when (feed) {
            TimeLineFeed.GROUPS -> {
                RepositoryResult.Error(RepositoryError.Internal("Groups feed is not implemented yet"))
            }

            TimeLineFeed.SELF -> {
                val entities = fetchTimelineEntities(
                    where = FeedItemsTable.author eq viewerUuid,
                    limit = safeLimit,
                    offset = safeOffset
                )
                RepositoryResult.Success(entities.map { it.toDomain(userId) })
            }

            TimeLineFeed.FRIENDS -> {
                val friendIds = getAcceptedFriendIds(viewerUuid)

                // friends-feed = friends + self
                val where = if (friendIds.isEmpty()) {
                    FeedItemsTable.author eq viewerUuid
                } else {
                    (FeedItemsTable.author inList friendIds) or (FeedItemsTable.author eq viewerUuid)
                }

                val entities = fetchTimelineEntities(
                    where = where,
                    limit = safeLimit,
                    offset = safeOffset
                )

                val friendSet = friendIds.toHashSet()
                val provider: (Uuid) -> FriendshipStatus? = { authorUuid ->
                    if (friendSet.contains(authorUuid.toJavaUuid())) FriendshipStatus.ACCEPTED else null
                }

                RepositoryResult.Success(entities.map { it.toDomain(userId, provider) })
            }

            TimeLineFeed.DISCOVERY -> {
                val friendIds = getAcceptedFriendIds(viewerUuid)

                val where = if (friendIds.isEmpty()) {
                    FeedItemsTable.author neq viewerUuid
                } else {
                    (FeedItemsTable.author notInList friendIds) and (FeedItemsTable.author neq viewerUuid)
                }

                val entities = fetchTimelineEntities(
                    where = where,
                    limit = safeLimit,
                    offset = safeOffset
                )

                // Discovery = authors are NOT friends by definition (we filtered them out),
                // so provider is always null => just map without provider.
                RepositoryResult.Success(entities.map { it.toDomain(userId) })
            }
        }
    }

    override suspend fun updateItem(
        userId: Uuid,
        item: FeedItem
    ): RepositoryResult<TimelineItem> = dbQuery {
        val viewerUuid = userId.toJavaUuid()
        val errors = mutableListOf<RepositoryError.FieldError>()
        val itemEntity = TimelineItemEntity.findById(item.uuid.toJavaUuid())

        val userEntity = UserEntity
            .find { UsersTable.id eq viewerUuid }
            .firstOrNull()

        if (userEntity == null) {
            errors += RepositoryError.FieldError(field = "author", "this userId is not registered as User")
        }

        if (itemEntity != null && userEntity != itemEntity.feedItem.author) {
            errors += RepositoryError.FieldError(field = "author", "User is not the author of this item")
        }

        if (item.content.content.isBlankOrNullRichText()) {
            errors += RepositoryError.FieldError(field = "content", "Content cannot be empty")
        }

        if (errors.isNotEmpty()) {
            return@dbQuery RepositoryResult.Error(RepositoryError.BadRequest(
                errors = errors
            ))
        }

        require(itemEntity != null) { "TimelineItemEntity should not be null at this stage" }

        itemEntity.feedItem.updatedAt = LocalDateTime.nowUtc()
        itemEntity.content = item.content.content!!
        RepositoryResult.Success(itemEntity.toDomain(userId))
    }

    private fun fetchTimelineEntities(
        where: Op<Boolean>,
        limit: Int,
        offset: Long
    ): List<TimelineItemEntity> = TimelineItemsTable
            .join(
                FeedItemsTable,
                JoinType.INNER,
                additionalConstraint = { TimelineItemsTable.feedItemId eq FeedItemsTable.id }
            )
            .select(TimelineItemsTable.columns)
            .where { where }
            .orderBy(FeedItemsTable.createdAt, SortOrder.DESC)
            .offset(start = offset)
            .limit(count = limit)
            .map { TimelineItemEntity.wrapRow(it) }
}