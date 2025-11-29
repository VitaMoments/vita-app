package nl.fbdevelopment.healthyplatform.api.service

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import nl.fbdevelopment.healthyplatform.data.models.dto.auth.LoginDto
import nl.fbdevelopment.healthyplatform.data.models.dto.auth.RegistrationDto

class AuthServiceImpl(private val client: HttpClient) : AuthService {
    override suspend fun login(body: LoginDto): HttpResponse = client.post("/auth/register") {
        setBody(body)
    }
    override suspend fun logout() : HttpResponse = client.post("/auth/logout")
    override suspend fun register(body: RegistrationDto): HttpResponse = client.post("/auth/register") {
        setBody(body)
    }
    override suspend fun refreshTokens(): HttpResponse = client.post("/auth/refresh_tokens")
    override suspend fun session(): HttpResponse = client.get("/auth/session")
}