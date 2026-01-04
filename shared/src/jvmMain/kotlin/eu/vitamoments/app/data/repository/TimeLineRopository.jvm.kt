package eu.vitamoments.app.data.repository

import kotlinx.serialization.json.JsonObject
import eu.vitamoments.app.data.entities.TimeLinePostEntity
import eu.vitamoments.app.data.entities.UserEntity
import eu.vitamoments.app.data.enums.FriendshipStatus
import eu.vitamoments.app.data.enums.TimeLineFeed
import eu.vitamoments.app.data.mapper.entity.toDomain
import eu.vitamoments.app.data.models.domain.message.TimeLinePost
import eu.vitamoments.app.data.tables.TimeLinePostsTable
import eu.vitamoments.app.data.tables.UsersTable
import eu.vitamoments.app.dbHelpers.dbQuery
import eu.vitamoments.app.dbHelpers.queries.getAcceptedFriendIds
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.neq
import org.jetbrains.exposed.v1.core.notInList
import java.util.UUID
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

class JVMTimeLineRepository() : TimeLineRepository {

    override suspend fun createPost(
        userId: Uuid,
        content: JsonObject
    ): RepositoryResponse<TimeLinePost> = dbQuery {
        val userEntity = UserEntity.find{ UsersTable.id eq userId.toJavaUuid() }.firstOrNull() ?: return@dbQuery RepositoryResponse.Error.InvalidData("UserId", "This userId is not registered as User")

        val entity = TimeLinePostEntity.new {
            this.createdBy = userEntity
            this.content = content
        }

        RepositoryResponse.Success(
            body = entity.toDomain(userId)
        )
    }

    override suspend fun getTimeLine(
        userId: Uuid,
        feed: TimeLineFeed,
        limit: Int,
        offset: Long
    ): RepositoryResponse<List<TimeLinePost>> = dbQuery {
        val viewerUuid: UUID = userId.toJavaUuid()

        when (feed) {
            TimeLineFeed.GROUPS -> {
                // TODO: later group posts bundlen
                return@dbQuery RepositoryResponse.Error.Internal("Groups feed is not implemented yet")
            }

            TimeLineFeed.SELF -> {
                val entities = TimeLinePostEntity
                    .find { TimeLinePostsTable.createdBy eq viewerUuid }
                    .orderBy(TimeLinePostsTable.createdAt to SortOrder.DESC)
                    .limit(count = limit)
                    .offset(offset)
                    .toList()

                val domain = entities.map { it.toDomain(userId) }
                return@dbQuery RepositoryResponse.Success(domain)
            }

            TimeLineFeed.FRIENDS -> {
                val friendIds = getAcceptedFriendIds(viewerUuid)
                if (friendIds.isEmpty()) return@dbQuery RepositoryResponse.Success(emptyList())

                val entities = TimeLinePostEntity
                    .find { TimeLinePostsTable.createdBy inList friendIds }
                    .orderBy(TimeLinePostsTable.createdAt to SortOrder.DESC)
                    .limit(count = limit)
                    .offset(offset)
                    .toList()

                val friendSet = friendIds.toHashSet()
                val provider: (Uuid) -> FriendshipStatus? = { authorUuid ->
                    if (friendSet.contains(authorUuid.toJavaUuid())) FriendshipStatus.ACCEPTED else null
                }

                val domain = entities.map { it.toDomain(userId, provider) }
                return@dbQuery RepositoryResponse.Success(domain)
            }

            TimeLineFeed.DISCOVERY -> {
                val friendIds = getAcceptedFriendIds(viewerUuid)
                val friendSet = friendIds.toHashSet()

                val entities = TimeLinePostEntity
                    .find {
                        (TimeLinePostsTable.createdBy neq viewerUuid) and
                                (if (friendIds.isEmpty()) org.jetbrains.exposed.v1.core.Op.TRUE
                                else TimeLinePostsTable.createdBy notInList friendIds)
                    }
                    .orderBy(TimeLinePostsTable.createdAt to SortOrder.DESC)
                    .limit(count = limit)
                    .offset(offset)
                    .toList()

                // Discovery = niet-vrienden => PUBLIC (behalve als author == viewer, maar dat filteren we al uit)
                val provider: (Uuid) -> FriendshipStatus? = { authorUuid ->
                    if (friendSet.contains(authorUuid.toJavaUuid())) FriendshipStatus.ACCEPTED else null
                }

                val domain = entities.map { it.toDomain(userId, provider) }
                return@dbQuery RepositoryResponse.Success(domain)
            }
        }
    }

}