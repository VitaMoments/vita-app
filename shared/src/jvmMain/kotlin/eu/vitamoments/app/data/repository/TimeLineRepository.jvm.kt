package eu.vitamoments.app.data.repository

import kotlinx.serialization.json.JsonObject
import eu.vitamoments.app.data.entities.TimeLineItemEntity
import eu.vitamoments.app.data.entities.UserEntity
import eu.vitamoments.app.data.models.enums.FriendshipStatus
import eu.vitamoments.app.data.models.enums.TimeLineFeed
import eu.vitamoments.app.data.mapper.entity.toDomain
import eu.vitamoments.app.data.models.domain.feed.TimelineItem
import eu.vitamoments.app.data.tables.TimeLineItemsTable
import eu.vitamoments.app.data.tables.UsersTable
import eu.vitamoments.app.dbHelpers.dbQuery
import eu.vitamoments.app.dbHelpers.queries.getAcceptedFriendIds
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.neq
import org.jetbrains.exposed.v1.core.notInList
import org.jetbrains.exposed.v1.core.or
import java.util.UUID
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

class JVMTimeLineRepository : TimeLineRepository {

    override suspend fun createPost(
        userId: Uuid,
        content: JsonObject
    ): RepositoryResult<TimelineItem> = dbQuery {
        val viewerUuid = userId.toJavaUuid()

        //todo: maak van de Error.NotFound een InvalidData zodat NotFound meer slaat op routes
        val userEntity = UserEntity
            .find { UsersTable.id eq viewerUuid }
            .firstOrNull()
            ?: return@dbQuery RepositoryResult.Error(RepositoryError.NotFound(
                message = "This userId is not registered as User"
            ))

        val entity = TimeLineItemEntity.new {
            this.createdBy = userEntity
            this.content = content
        }

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
                val entities = TimeLineItemEntity
                    .find { TimeLineItemsTable.createdBy eq viewerUuid }
                    .orderBy(TimeLineItemsTable.createdAt to SortOrder.DESC)
                    .limit(count = safeLimit)
                    .offset(safeOffset)
                    .toList()

                RepositoryResult.Success(entities.map { it.toDomain(userId) })
            }

            TimeLineFeed.FRIENDS -> {
                val friendIds = getAcceptedFriendIds(viewerUuid)

                // friends-feed = friends + self
                val where = if (friendIds.isEmpty()) {
                    TimeLineItemsTable.createdBy eq viewerUuid
                } else {
                    (TimeLineItemsTable.createdBy inList friendIds) or
                            (TimeLineItemsTable.createdBy eq viewerUuid)
                }

                val entities = TimeLineItemEntity
                    .find { where }
                    .orderBy(TimeLineItemsTable.createdAt to SortOrder.DESC)
                    .limit(count = safeLimit)
                    .offset(safeOffset)
                    .toList()

                val friendSet = friendIds.toHashSet()
                val provider: (Uuid) -> FriendshipStatus? = { authorUuid ->
                    if (friendSet.contains(authorUuid.toJavaUuid())) FriendshipStatus.ACCEPTED else null
                }

                RepositoryResult.Success(entities.map { it.toDomain(userId, provider) })
            }

            TimeLineFeed.DISCOVERY -> {
                val friendIds = getAcceptedFriendIds(viewerUuid)

                val where = if (friendIds.isEmpty()) {
                    // not me
                    TimeLineItemsTable.createdBy neq viewerUuid
                } else {
                    // not me AND not friends
                    (TimeLineItemsTable.createdBy neq viewerUuid) and
                            (TimeLineItemsTable.createdBy notInList friendIds)
                }

                val entities = TimeLineItemEntity
                    .find { where }
                    .orderBy(TimeLineItemsTable.createdAt to SortOrder.DESC)
                    .limit(count = safeLimit)
                    .offset(safeOffset)
                    .toList()

                // Discovery = authors are NOT friends by definition (we filtered them out),
                // so provider is always null => just map without provider.
                RepositoryResult.Success(entities.map { it.toDomain(userId) })
            }
        }
    }
}
