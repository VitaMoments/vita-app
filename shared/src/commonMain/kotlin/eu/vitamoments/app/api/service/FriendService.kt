package eu.vitamoments.app.api.service

import eu.vitamoments.app.data.models.requests.friendship_requests.InviteFriendshipRequest
import eu.vitamoments.app.data.models.requests.friendship_requests.UpdateFriendshipRequest
import io.ktor.client.statement.HttpResponse

interface FriendService {
    suspend fun searchNewFriends(query: String?, limit: Int = 20, offset: Int = 0) : HttpResponse
    suspend fun searchFriends(query: String?, limit: Int = 20, offset: Int = 0) : HttpResponse
    suspend fun invite(
        body: InviteFriendshipRequest
    ) : HttpResponse
    suspend fun updateFriendship(
        body: UpdateFriendshipRequest
    ) : HttpResponse
}