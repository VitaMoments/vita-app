package eu.vitamoments.app.data.repository

import eu.vitamoments.app.data.entities.DailyQuestionAnswerEntity
import eu.vitamoments.app.data.entities.TimelineItemEntity
import eu.vitamoments.app.data.entities.UserEntity
import eu.vitamoments.app.data.factory.FeedItemFactory
import eu.vitamoments.app.data.mapper.entity.toDomain
import eu.vitamoments.app.data.mapper.entity.toFeedDomain
import eu.vitamoments.app.data.mapper.extension_functions.nowUtc
import eu.vitamoments.app.data.models.domain.feed.FeedItem
import eu.vitamoments.app.data.models.domain.feed.TimelineItem
import eu.vitamoments.app.data.models.enums.FeedItemType
import eu.vitamoments.app.data.models.enums.FriendshipStatus
import eu.vitamoments.app.data.models.enums.TimeLineFeed
import eu.vitamoments.app.data.models.helpers.extension_functions.isBlankOrNullRichText
import eu.vitamoments.app.data.models.helpers.extension_functions.isBlankRichText
import eu.vitamoments.app.data.tables.DailyQuestionAnswersTable
import eu.vitamoments.app.data.tables.FeedItemsTable
import eu.vitamoments.app.data.tables.UsersTable
import eu.vitamoments.app.dbHelpers.dbQuery
import eu.vitamoments.app.dbHelpers.queries.getAcceptedFriendIds
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.JsonElement
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.neq
import org.jetbrains.exposed.v1.core.notInList
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.select
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
            author = userEntity!!,
            content = content
        )

        RepositoryResult.Success(entity.toDomain(userId))
    }

    override suspend fun getTimeLine(
        userId: Uuid,
        feed: TimeLineFeed,
        limit: Int,
        offset: Long
    ): RepositoryResult<List<FeedItem>> = dbQuery {
        val viewerUuid: UUID = userId.toJavaUuid()

        val safeLimit = limit.coerceIn(1, 100)
        val safeOffset = offset.coerceAtLeast(0L)
        val fetchCount = (safeOffset + safeLimit).coerceAtMost(1000L).toInt()

        when (feed) {
            TimeLineFeed.GROUPS -> {
                RepositoryResult.Error(RepositoryError.Internal("Groups feed is not implemented yet"))
            }

            TimeLineFeed.SELF -> {
                val timelineEntities = fetchTimelineFeedEntities(
                    where = FeedItemsTable.author eq viewerUuid,
                    limit = fetchCount,
                )
                val answerEntities = fetchDailyAnswerEntities(
                    where = DailyQuestionAnswersTable.userId eq viewerUuid,
                    limit = fetchCount
                )

                val feedItems = (timelineEntities.map { it.toDomain(userId) } +
                    answerEntities.map { it.toFeedDomain(userId) })
                    .sortedByDescending { it.createdAt }
                    .drop(safeOffset.toInt())
                    .take(safeLimit)

                RepositoryResult.Success(feedItems)
            }

            TimeLineFeed.FRIENDS -> {
                val friendIds = getAcceptedFriendIds(viewerUuid)

                val where = if (friendIds.isEmpty()) {
                    FeedItemsTable.author eq viewerUuid
                } else {
                    (FeedItemsTable.author inList friendIds) or (FeedItemsTable.author eq viewerUuid)
                }

                val timelineEntities = fetchTimelineFeedEntities(
                    where = where,
                    limit = fetchCount,
                )

                val friendSet = friendIds.toHashSet()
                val provider: (Uuid) -> FriendshipStatus? = { authorUuid ->
                    if (friendSet.contains(authorUuid.toJavaUuid())) FriendshipStatus.ACCEPTED else null
                }

                val answerWhere = if (friendIds.isEmpty()) {
                    DailyQuestionAnswersTable.userId eq viewerUuid
                } else {
                    (DailyQuestionAnswersTable.userId inList friendIds) or (DailyQuestionAnswersTable.userId eq viewerUuid)
                }
                val answerEntities = fetchDailyAnswerEntities(answerWhere, fetchCount)

                val feedItems = (timelineEntities.map { it.toDomain(userId, provider) } +
                    answerEntities.map { it.toFeedDomain(userId, provider) })
                    .sortedByDescending { it.createdAt }
                    .drop(safeOffset.toInt())
                    .take(safeLimit)

                RepositoryResult.Success(feedItems)
            }

            TimeLineFeed.DISCOVERY -> {
                val friendIds = getAcceptedFriendIds(viewerUuid)

                val where = if (friendIds.isEmpty()) {
                    FeedItemsTable.author neq viewerUuid
                } else {
                    (FeedItemsTable.author notInList friendIds) and (FeedItemsTable.author neq viewerUuid)
                }

                val timelineEntities = fetchTimelineFeedEntities(
                    where = where,
                    limit = fetchCount,
                )

                val answerWhere = if (friendIds.isEmpty()) {
                    DailyQuestionAnswersTable.userId neq viewerUuid
                } else {
                    (DailyQuestionAnswersTable.userId notInList friendIds) and (DailyQuestionAnswersTable.userId neq viewerUuid)
                }
                val answerEntities = fetchDailyAnswerEntities(answerWhere, fetchCount)

                val feedItems = (timelineEntities.map { it.toDomain(userId) } +
                    answerEntities.map { it.toFeedDomain(userId) })
                    .sortedByDescending { it.createdAt }
                    .drop(safeOffset.toInt())
                    .take(safeLimit)

                RepositoryResult.Success(feedItems)
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

    private fun fetchTimelineFeedEntities(
        where: Op<Boolean>,
        limit: Int,
    ): List<TimelineItemEntity> {
        val ids = FeedItemsTable
            .select(FeedItemsTable.id)
            .where { where and (FeedItemsTable.type eq FeedItemType.TIMELINE) }
            .orderBy(FeedItemsTable.createdAt, SortOrder.DESC)
            .limit(count = limit)
            .map { it[FeedItemsTable.id].value }

        return ids.mapNotNull { TimelineItemEntity.findById(it) }
    }

    private fun fetchDailyAnswerEntities(
        where: Op<Boolean>,
        limit: Int,
    ): List<DailyQuestionAnswerEntity> = DailyQuestionAnswerEntity
        .find { where }
        .orderBy(DailyQuestionAnswersTable.answeredAt to SortOrder.DESC)
        .limit(limit)
        .toList()
}