package eu.vitamoments.app.api.service

import io.ktor.client.statement.HttpResponse
import kotlin.uuid.Uuid

interface UserService {
    suspend fun getUser(userId: Uuid): HttpResponse
    suspend fun searchUsers(query: String?, limit: Int = 20, offset: Int = 0) : HttpResponse
    suspend fun getMyAccount(): HttpResponse
    suspend fun updateMyAccount(): HttpResponse
    suspend fun updateMyProfileImage(): HttpResponse
}