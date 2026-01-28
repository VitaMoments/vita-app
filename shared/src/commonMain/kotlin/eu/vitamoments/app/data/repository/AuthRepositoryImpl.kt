package eu.vitamoments.app.data.repository

import eu.vitamoments.app.api.service.AuthService
import eu.vitamoments.app.data.mapper.extension_functions.map
import eu.vitamoments.app.data.mapper.toRepositoryResponse
import eu.vitamoments.app.data.models.domain.AuthSession
import eu.vitamoments.app.data.models.domain.user.User
import eu.vitamoments.app.data.models.requests.auth_requests.LoginRequest
import eu.vitamoments.app.data.models.requests.auth_requests.RegistrationRequest

class AuthRepositoryImpl(
    private val service: AuthService
) : AuthRepository<User> {

    override suspend fun login(email: String, password: String): RepositoryResponse<User> {
        val requestBody = LoginRequest(
            email = email,
            password = password
        )

        val response = service.login(body = requestBody)
        return response
            .toRepositoryResponse<AuthSession>()
            .map { session -> session.user }
    }

    override suspend fun register(
        username: String,
        email: String,
        password: String
    ): RepositoryResponse<User> {
        val requestBody = RegistrationRequest(
            username = username,
            email = email,
            password = password
        )
        val response = service.register(body = requestBody)
        return response
            .toRepositoryResponse<AuthSession>()
            .map { session -> session.user }
    }

    override suspend fun logout(refreshToken: String): RepositoryResponse<Boolean> {
        val response = service.logout()
        return response.toRepositoryResponse<Boolean>()
    }

    override suspend fun refresh(refreshToken: String?): RepositoryResponse<User> {
        val response = service.refreshTokens()
        return response
            .toRepositoryResponse<AuthSession>()
            .map { session -> session.user }
    }

    @Suppress("DEPRECATION")
    override suspend fun session(): RepositoryResponse<User> {
        val response = service.session()
        return response
            .toRepositoryResponse<AuthSession>()
            .map { session -> session.user }
    }
}