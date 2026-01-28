package eu.vitamoments.app.data.repository

import eu.vitamoments.app.data.models.domain.user.Friendship
import eu.vitamoments.app.data.models.domain.user.PublicUser
import eu.vitamoments.app.data.models.domain.user.UserWithContext
import eu.vitamoments.app.data.models.domain.common.PagedResult
import kotlin.uuid.Uuid

interface FriendRepository {
    suspend fun searchNewFriends(
        userId: Uuid,
        query: String? = null,
        limit: Int = 20,
        offset: Int = 0) : RepositoryResponse<PagedResult<PublicUser>>
    suspend fun searchFriends(
        userId: Uuid,
        query: String? = null,
        limit: Int = 20,
        offset: Int = 0) : RepositoryResponse<PagedResult<UserWithContext>>
    suspend fun incomingRequests(
        userId: Uuid
    ) : RepositoryResponse<List<PublicUser>>
    suspend fun outgoingRequests(
        userId: Uuid
    ) : RepositoryResponse<List<PublicUser>>
    suspend fun invite(
        userId: Uuid,
        otherId: Uuid
    ) : RepositoryResponse<Friendship>
    suspend fun accept(
        userId: Uuid,
        otherId: Uuid
    ): RepositoryResponse<Friendship>
    suspend fun delete(
        userId: Uuid,
        otherId: Uuid
    ): RepositoryResponse<Friendship>
    suspend fun decline(
        userId: Uuid,
        otherId: Uuid
    ): RepositoryResponse<Friendship>

    suspend fun friendRequests(
        userId: Uuid,
        query: String?,
        limit: Int,
        offset: Int
    ): RepositoryResponse<PagedResult<UserWithContext>>
}