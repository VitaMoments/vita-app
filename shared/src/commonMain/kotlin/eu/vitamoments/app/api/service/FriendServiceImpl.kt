package eu.vitamoments.app.api.service

import eu.vitamoments.app.data.models.requests.friendship_requests.InviteFriendshipRequest
import eu.vitamoments.app.data.models.requests.friendship_requests.UpdateFriendshipRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse

class FriendServiceImpl(private val client: HttpClient) : FriendService {
    override suspend fun searchNewFriends(
        query: String?,
        limit: Int,
        offset: Int
    ): HttpResponse = client.get("/friends/new") {
        parameter("query", query)
        parameter("limit", limit)
        parameter("offset", offset)
    }

    override suspend fun searchFriends(
        query: String?,
        limit: Int,
        offset: Int
    ): HttpResponse = client.get("/friends") {
        parameter("query", query)
        parameter("limit", limit)
        parameter("offset", offset)
    }

    override suspend fun invite(body: InviteFriendshipRequest): HttpResponse = client.post("/friends/invite") {
        setBody(body)
    }

    override suspend fun updateFriendship(body: UpdateFriendshipRequest): HttpResponse = client.post("/friends/update") {
        setBody(body)
    }
}