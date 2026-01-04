@file:OptIn(ExperimentalUuidApi::class)

package eu.vitamoments.app.api.service

import eu.vitamoments.app.data.enums.FriendshipStatus
import eu.vitamoments.app.data.models.dto.user.FriendInviteDto
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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

    override suspend fun invite(
        body: FriendInviteDto
    ): HttpResponse = client.post("/friends/invite") {
        setBody(body)
    }

    override suspend fun setFriendshipStatus(
        body: FriendInviteDto
    ): HttpResponse {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFriendship(
        body: FriendInviteDto
    ): HttpResponse {
        TODO("Not yet implemented")
    }
}