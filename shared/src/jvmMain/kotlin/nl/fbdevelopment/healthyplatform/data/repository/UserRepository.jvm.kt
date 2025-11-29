@file:OptIn(ExperimentalUuidApi::class)

package nl.fbdevelopment.healthyplatform.data.repository

import kotlinx.datetime.LocalDateTime
import nl.fbdevelopment.healthyplatform.data.entities.UserEntity
import nl.fbdevelopment.healthyplatform.data.mapper.enitity.toDomain
import nl.fbdevelopment.healthyplatform.data.mapper.extension_functions.nowUtc
import nl.fbdevelopment.healthyplatform.data.models.domain.user.User
import nl.fbdevelopment.healthyplatform.dbHelpers.dbQuery
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

class JVMUserRepository() : UserRepository {
    override suspend fun getUserById(uuid: Uuid): RepositoryResponse<User> = dbQuery {
        val entity = UserEntity.findById(uuid.toJavaUuid())

        if (entity == null) {
            RepositoryResponse.Error.NotFound(
                message = "User with id $uuid not found"
            )
        } else {
            RepositoryResponse.Success(
                body = entity.toDomain()
            )
        }
    }

    override suspend fun updateUser(user: User): RepositoryResponse<User> = dbQuery {
        val entity = UserEntity.findByIdAndUpdate(id = user.uuid.toJavaUuid()) {
            it.updatedAt = LocalDateTime.nowUtc()
            it.imageUrl = user.imageUrl
        }

        if (entity == null) {
            RepositoryResponse.Error.NotFound(
                message = "User with id ${user.uuid} not found"
            )
        } else {
            RepositoryResponse.Success(
                body = entity.toDomain()
            )
        }
    }

}