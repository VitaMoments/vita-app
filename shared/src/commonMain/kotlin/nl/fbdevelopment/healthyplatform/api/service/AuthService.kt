package nl.fbdevelopment.healthyplatform.api.service

import io.ktor.client.statement.HttpResponse
import nl.fbdevelopment.healthyplatform.data.models.dto.auth.AuthTokenDto
import nl.fbdevelopment.healthyplatform.data.models.dto.auth.LoginDto
import nl.fbdevelopment.healthyplatform.data.models.dto.auth.RegistrationDto

interface AuthService {
    suspend fun login(body: LoginDto) : HttpResponse
    suspend fun logout() : HttpResponse
    suspend fun register(body: RegistrationDto): HttpResponse
    suspend fun refreshTokens() : HttpResponse
    suspend fun session(): HttpResponse
}