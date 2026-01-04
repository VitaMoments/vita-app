package eu.vitamoments.app.data.repository

import eu.vitamoments.app.data.models.domain.user.User

interface AuthRepository<TAuth> {
    suspend fun login(email: String, password: String) : RepositoryResponse<TAuth>
    suspend fun register(username: String, email: String, password: String) : RepositoryResponse<TAuth>
    suspend fun logout(refreshToken: String) : RepositoryResponse<Boolean>
    suspend fun refresh(refreshToken: String? = null) : RepositoryResponse<TAuth>
    suspend fun session() : RepositoryResponse<TAuth>
}

typealias ClientAuthRepository = AuthRepository<User>

