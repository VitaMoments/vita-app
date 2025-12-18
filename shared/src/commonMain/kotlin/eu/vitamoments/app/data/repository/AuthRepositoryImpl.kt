@file:OptIn(ExperimentalUuidApi::class)

package eu.vitamoments.app.data.repository

import eu.vitamoments.app.api.service.AuthService
import eu.vitamoments.app.data.mapper.toDomain
import eu.vitamoments.app.data.mapper.toRepositoryResponse
import eu.vitamoments.app.data.models.domain.user.User
import eu.vitamoments.app.data.models.dto.auth.AuthSessionDto
import eu.vitamoments.app.data.models.dto.auth.LoginDto
import eu.vitamoments.app.data.models.dto.auth.RegistrationDto
import kotlin.uuid.ExperimentalUuidApi

class AuthRepositoryImpl(
    private val service: AuthService
) : AuthRepository<User> {

    override suspend fun login(email: String, password: String): RepositoryResponse<User> {
        val dto = LoginDto(
            email = email,
            password = password)
        val response = service.login(body = dto)

        return response.toRepositoryResponse<AuthSessionDto, User> { dto ->
            dto.user.toDomain()
        }
    }

    override suspend fun register(
        email: String,
        password: String
    ): RepositoryResponse<User> {
        val dto = RegistrationDto(
            email = email,
            password = password
        )
        val response = service.register(body = dto)
        return response.toRepositoryResponse<AuthSessionDto, User> { dto ->
            dto.user.toDomain()
        }
    }

    override suspend fun logout(refreshToken: String): RepositoryResponse<Boolean> {
        val response = service.logout()
        return response.toRepositoryResponse<Boolean>()
    }

    override suspend fun refresh(refreshToken: String?): RepositoryResponse<User> {
        val response = service.refreshTokens()
        return response.toRepositoryResponse<AuthSessionDto, User> { dto -> dto.user.toDomain() }
    }

    @Suppress("DEPRECATION")
    override suspend fun session(): RepositoryResponse<User> {
        val response = service.session()
        return response.toRepositoryResponse<AuthSessionDto, User> {dto -> dto.user.toDomain()}
    }
}