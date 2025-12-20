@file:OptIn(ExperimentalUuidApi::class)

package eu.vitamoments.app.data.repository

import eu.vitamoments.app.data.models.domain.user.User
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface UserRepository {
    suspend fun getUserById(uuid: Uuid) : RepositoryResponse<User>
    suspend fun updateUser(user: User) : RepositoryResponse<User>
    suspend fun updateImageUrl(userId: Uuid, url: String) : RepositoryResponse<User>
}