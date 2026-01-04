package eu.vitamoments.app.data.repository

import eu.vitamoments.app.data.models.domain.user.Friendship
import eu.vitamoments.app.data.models.domain.user.PrivateUser
import eu.vitamoments.app.data.models.domain.user.PublicUser
import kotlin.uuid.Uuid

interface FriendRepository {
    suspend fun searchNewFriends(
        userId: Uuid,
        query: String? = null,
        limit: Int = 20,
        offset: Int = 0) : RepositoryResponse<List<PublicUser>>
    suspend fun searchFriends(
        userId: Uuid,
        query: String? = null,
        limit: Int = 20,
        offset: Int = 0) : RepositoryResponse<List<PrivateUser>>
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
}