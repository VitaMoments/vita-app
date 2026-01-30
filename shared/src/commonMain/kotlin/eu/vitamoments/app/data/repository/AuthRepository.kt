package eu.vitamoments.app.data.repository

import eu.vitamoments.app.data.models.domain.user.AccountUser

interface AuthRepository<TAuth> {
    suspend fun login(email: String, password: String) : RepositoryResult<TAuth>
    suspend fun register(username: String, email: String, password: String) : RepositoryResult<TAuth>
    suspend fun logout(refreshToken: String) : RepositoryResult<Unit>
    suspend fun refresh(refreshToken: String? = null) : RepositoryResult<TAuth>
    suspend fun session() : RepositoryResult<TAuth>
}

typealias ClientAuthRepository = AuthRepository<AccountUser>

