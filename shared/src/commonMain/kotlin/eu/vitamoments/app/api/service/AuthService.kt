package eu.vitamoments.app.api.service

import eu.vitamoments.app.data.models.requests.auth_requests.LoginRequest
import eu.vitamoments.app.data.models.requests.auth_requests.RegistrationRequest
import io.ktor.client.statement.HttpResponse

interface AuthService {
    suspend fun login(body: LoginRequest) : HttpResponse
    suspend fun logout() : HttpResponse
    suspend fun register(body: RegistrationRequest): HttpResponse
    suspend fun refreshTokens() : HttpResponse
    suspend fun session(): HttpResponse
}