package eu.vitamoments.app.data.repository

import eu.vitamoments.app.data.entities.UserEntity
import eu.vitamoments.app.data.models.enums.FriendshipStatus
import eu.vitamoments.app.data.mapper.entity.toAccountDomain
import eu.vitamoments.app.data.mapper.entity.toPrivateDomain
import eu.vitamoments.app.data.mapper.entity.toPublicDomain
import eu.vitamoments.app.data.mapper.extension_functions.nowUtc
import eu.vitamoments.app.data.models.domain.user.AccountUser
import eu.vitamoments.app.data.models.domain.user.User
import eu.vitamoments.app.dbHelpers.dbQuery
import eu.vitamoments.app.dbHelpers.queries.findFriendshipByPair
import kotlinx.datetime.LocalDateTime
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

class JVMUserRepository() : UserRepository {
    override suspend fun getUser(currentUserId: Uuid, userId: Uuid): RepositoryResponse<User> = dbQuery {
        val entity = UserEntity.findById(userId.toJavaUuid())
            ?: return@dbQuery RepositoryResponse.Error.NotFound("User with id: $userId not found")

        if (currentUserId == userId) {
            return@dbQuery RepositoryResponse.Success(entity.toAccountDomain())
        }

        val friends = findFriendshipByPair(currentUserId.toJavaUuid(), userId.toJavaUuid())
        RepositoryResponse.Success(if (friends?.status == FriendshipStatus.ACCEPTED) entity.toPrivateDomain() else entity.toPublicDomain())
    }

    override suspend fun getMyAccount(userId: Uuid): RepositoryResponse<AccountUser> = dbQuery {
        val entity = UserEntity.findById(userId.toJavaUuid()) ?: return@dbQuery RepositoryResponse.Error.NotFound("User with id: $userId not found")
        RepositoryResponse.Success(entity.toAccountDomain())
    }

    override suspend fun updateMyAccount(): RepositoryResponse<AccountUser> {
        TODO("Not yet implemented")
    }

    override suspend fun updateMyProfileImage(userId: Uuid, url: String): RepositoryResponse<AccountUser> = dbQuery {
        val entity = UserEntity.findByIdAndUpdate(id = userId.toJavaUuid()) {
            it.updatedAt = LocalDateTime.nowUtc()
            it.imageUrl = url
        }

        if (entity == null) {
            RepositoryResponse.Error.NotFound(
                message = "User with id $userId not found"
            )
        } else {
            RepositoryResponse.Success(
                body = entity.toAccountDomain()
            )
        }
    }
}