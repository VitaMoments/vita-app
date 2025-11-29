@file:OptIn(ExperimentalJsExport::class, ExperimentalUuidApi::class)

package nl.fbdevelopment.healthyplatform.data.repository

import nl.fbdevelopment.healthyplatform.data.models.domain.user.User
import kotlin.js.ExperimentalJsExport
import kotlin.uuid.ExperimentalUuidApi

interface AuthRepository<TAuth> {
    suspend fun login(email: String, password: String) : RepositoryResponse<TAuth>
    suspend fun register(email: String, password: String) : RepositoryResponse<TAuth>
    suspend fun logout(refreshToken: String) : RepositoryResponse<Boolean>
    suspend fun refresh(refreshToken: String? = null) : RepositoryResponse<TAuth>
    suspend fun session() : RepositoryResponse<TAuth>
}

typealias ClientAuthRepository = AuthRepository<User>

