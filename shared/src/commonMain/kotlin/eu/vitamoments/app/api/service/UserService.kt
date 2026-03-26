package eu.vitamoments.app.api.service

import io.ktor.client.statement.HttpResponse
import eu.vitamoments.app.data.models.requests.user_requests.UpdateMyAccountRequest
import kotlin.uuid.Uuid

interface UserService {
    suspend fun getUser(userId: Uuid): HttpResponse
    suspend fun searchUsers(query: String?, limit: Int = 20, offset: Int = 0) : HttpResponse
    suspend fun getMyAccount(): HttpResponse
    suspend fun updateMyAccount(request: UpdateMyAccountRequest): HttpResponse
    suspend fun updateMyProfileImage(): HttpResponse
}