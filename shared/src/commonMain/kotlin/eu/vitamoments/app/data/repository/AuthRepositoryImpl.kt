package eu.vitamoments.app.data.repository

import eu.vitamoments.app.api.helpers.safeCall
import eu.vitamoments.app.api.service.AuthService
import eu.vitamoments.app.data.models.domain.user.AccountUser
import eu.vitamoments.app.data.models.requests.auth_requests.LoginRequest
import eu.vitamoments.app.data.models.requests.auth_requests.RegistrationRequest

class AuthRepositoryImpl(
    private val service: AuthService
) : AuthRepository<AccountUser> {
    override suspend fun login(email: String, password: String): RepositoryResult<AccountUser> = safeCall {
        service.login(body = LoginRequest(
            email = email,
            password = password)) }

    override suspend fun register(
        username: String,
        email: String,
        password: String
    ): RepositoryResult<AccountUser> = safeCall{
        service.register(body = RegistrationRequest(
            username = username,
            email = email,
            password = password)) }

    override suspend fun logout(refreshToken: String): RepositoryResult<Unit> =
        safeCall { service.logout() }

    override suspend fun refresh(refreshToken: String?): RepositoryResult<AccountUser> {
        val refreshed: RepositoryResult<Unit> = safeCall { service.refreshTokens() }
        if (refreshed is RepositoryResult.Error) return refreshed
        return safeCall { service.session() }
    }

    override suspend fun session(): RepositoryResult<AccountUser> =
        safeCall { service.session() }
}