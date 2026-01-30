package eu.vitamoments.app.data.repository

import eu.vitamoments.app.data.models.domain.user.AccountUser
import eu.vitamoments.app.data.models.domain.user.User
import kotlin.uuid.Uuid

interface UserRepository {
    suspend fun getUser(currentUserId: Uuid, userId: Uuid): RepositoryResult<User>
    suspend fun getMyAccount(userId: Uuid): RepositoryResult<AccountUser>
    suspend fun updateMyAccount(): RepositoryResult<AccountUser>
    suspend fun updateMyProfileImage(userId: Uuid, url: String): RepositoryResult<AccountUser>
}