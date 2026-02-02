package eu.vitamoments.app.data.repository

import eu.vitamoments.app.api.service.FriendService
import eu.vitamoments.app.data.mapper.toRepositoryResult
import eu.vitamoments.app.data.models.domain.friendship.Friendship
import eu.vitamoments.app.data.models.domain.user.PublicUser
import eu.vitamoments.app.data.models.domain.user.UserWithContext
import eu.vitamoments.app.data.models.domain.common.PagedResult
import eu.vitamoments.app.data.models.requests.friendship_requests.InviteFriendshipRequest
import kotlin.uuid.Uuid

class FriendRepositoryImpl(private val service: FriendService) : FriendRepository {
    override suspend fun searchNewFriends(
        userId: Uuid,
        query: String?,
        limit: Int,
        offset: Int
    ) : RepositoryResult<PagedResult<PublicUser>> {
       TODO("Update this")
    }

    override suspend fun searchFriends(
        userId: Uuid,
        query: String?,
        limit: Int,
        offset: Int
    ) : RepositoryResult<PagedResult<UserWithContext>> {
//        val response = service.searchNewFriends(query, limit, offset)
//        return response.toRepositoryResponse<List<PrivateUserDto>, List<PrivateUser>> { listDto -> listDto.map { dto-> dto.toDomain() } }
        TODO("Implementation needs changing after update")
    }

    override suspend fun incomingRequests(userId: Uuid): RepositoryResult<List<PublicUser>> {
        TODO("Not yet implemented")
    }

    override suspend fun outgoingRequests(userId: Uuid): RepositoryResult<List<PublicUser>> {
        TODO("Not yet implemented")
    }

    override suspend fun invite(
        userId: Uuid,
        otherId: Uuid
    ): RepositoryResult<Friendship> = service
        .invite(InviteFriendshipRequest(userId = otherId))
        .toRepositoryResult()


    override suspend fun accept(
        userId: Uuid,
        otherId: Uuid
    ): RepositoryResult<Friendship> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(
        userId: Uuid,
        otherId: Uuid
    ): RepositoryResult<Friendship> {
        TODO("Not yet implemented")
    }

    override suspend fun decline(
        userId: Uuid,
        otherId: Uuid
    ): RepositoryResult<Friendship> {
        TODO("Not yet implemented")
    }

    override suspend fun friendRequests(
        userId: Uuid,
        query: String?,
        limit: Int,
        offset: Int
    ): RepositoryResult<PagedResult<UserWithContext>> {
        TODO("Not yet implemented")
    }
}