package eu.vitamoments.app.data.repository

import eu.vitamoments.app.api.service.FriendService
import eu.vitamoments.app.data.mapper.toDomain
import eu.vitamoments.app.data.mapper.toRepositoryResponse
import eu.vitamoments.app.data.models.domain.user.Friendship
import eu.vitamoments.app.data.models.domain.user.PrivateUser
import eu.vitamoments.app.data.models.domain.user.PublicUser
import eu.vitamoments.app.data.models.domain.user.UserWithContext
import eu.vitamoments.app.data.models.domain.utils.PagedResult
import eu.vitamoments.app.data.models.dto.user.FriendInviteDto
import eu.vitamoments.app.data.models.dto.user.FriendshipDto
import eu.vitamoments.app.data.models.dto.user.PrivateUserDto
import eu.vitamoments.app.data.models.dto.user.UserDto
import eu.vitamoments.app.data.models.dto.user.UserWithContextDto
import kotlin.uuid.Uuid

class FriendRepositoryImpl(private val service: FriendService) : FriendRepository {
    override suspend fun searchNewFriends(
        userId: Uuid,
        query: String?,
        limit: Int,
        offset: Int
    ) : RepositoryResponse<PagedResult<PublicUser>> {
       TODO("Update this")
    }

    override suspend fun searchFriends(
        userId: Uuid,
        query: String?,
        limit: Int,
        offset: Int
    ) : RepositoryResponse<PagedResult<UserWithContext>> {
//        val response = service.searchNewFriends(query, limit, offset)
//        return response.toRepositoryResponse<List<PrivateUserDto>, List<PrivateUser>> { listDto -> listDto.map { dto-> dto.toDomain() } }
        TODO("Implementation needs changing after update")
    }

    override suspend fun incomingRequests(userId: Uuid): RepositoryResponse<List<PublicUser>> {
        TODO("Not yet implemented")
    }

    override suspend fun outgoingRequests(userId: Uuid): RepositoryResponse<List<PublicUser>> {
        TODO("Not yet implemented")
    }

    override suspend fun invite(
        userId: Uuid,
        receiverId: Uuid
    ): RepositoryResponse<Friendship> {
        val response = service.invite(FriendInviteDto(friendId = receiverId))
        return response.toRepositoryResponse<FriendshipDto, Friendship> { dto -> dto.toDomain() }
    }

    override suspend fun accept(
        userId: Uuid,
        otherId: Uuid
    ): RepositoryResponse<Friendship> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(
        userId: Uuid,
        otherId: Uuid
    ): RepositoryResponse<Friendship> {
        TODO("Not yet implemented")
    }

    override suspend fun decline(
        userId: Uuid,
        otherId: Uuid
    ): RepositoryResponse<Friendship> {
        TODO("Not yet implemented")
    }

    override suspend fun friendRequests(
        userId: Uuid,
        query: String?,
        limit: Int,
        offset: Int
    ): RepositoryResponse<PagedResult<UserWithContext>> {
        TODO("Not yet implemented")
    }
}