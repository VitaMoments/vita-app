@file:OptIn(ExperimentalUuidApi::class)

package eu.vitamoments.app.api.service

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import eu.vitamoments.app.data.models.dto.user.PrivateUserDto
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.parameters
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class UserServiceImpl(private val client: HttpClient) : UserService {
    override suspend fun getUser(userId: Uuid): HttpResponse = client.get("/users/$userId")

    override suspend fun searchUsers(
        query: String?,
        limit: Int,
        offset: Int
    ): HttpResponse = client.get("/users/search") {
        parameter("query", query)
        parameter("limit", limit)
        parameter("offset", offset)
    }

    override suspend fun getMyAccount(): HttpResponse = client.get("/account")

    override suspend fun updateMyAccount(): HttpResponse {
        TODO("Not yet implemented")
    }

    override suspend fun updateMyProfileImage(): HttpResponse {
        TODO("Not yet implemented")
    }
}