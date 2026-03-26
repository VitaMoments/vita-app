package eu.vitamoments.app.api.service

import io.ktor.client.HttpClient
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import eu.vitamoments.app.data.models.requests.user_requests.UpdateMyAccountRequest
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

    override suspend fun getMyAccount(): HttpResponse = client.get("/auth/session")

    override suspend fun updateMyAccount(request: UpdateMyAccountRequest): HttpResponse =
        client.put("/profile") {
            setBody(request)
        }

    override suspend fun updateMyProfileImage(): HttpResponse {
        TODO("Not yet implemented")
    }
}