@file:OptIn(ExperimentalUuidApi::class)

package eu.vitamoments.app.data.repository

import kotlinx.datetime.LocalDateTime
import eu.vitamoments.app.data.entities.UserEntity
import eu.vitamoments.app.data.mapper.enitity.toDomain
import eu.vitamoments.app.data.mapper.extension_functions.nowUtc
import eu.vitamoments.app.data.models.domain.user.User
import eu.vitamoments.app.dbHelpers.dbQuery
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