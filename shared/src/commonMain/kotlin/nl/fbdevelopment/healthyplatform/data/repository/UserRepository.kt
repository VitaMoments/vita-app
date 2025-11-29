@file:OptIn(ExperimentalUuidApi::class)

package nl.fbdevelopment.healthyplatform.data.repository

import nl.fbdevelopment.healthyplatform.data.models.domain.user.User
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface UserRepository {
    suspend fun getUserById(uuid: Uuid) : RepositoryResponse<User>
    suspend fun updateUser(user: User) : RepositoryResponse<User>
}