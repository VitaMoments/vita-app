package eu.vitamoments.app.data.repository

import eu.vitamoments.app.data.entities.UserEntity
import eu.vitamoments.app.data.models.enums.FriendshipStatus
import eu.vitamoments.app.data.mapper.entity.toAccountDomain
import eu.vitamoments.app.data.mapper.entity.toPrivateDomain
import eu.vitamoments.app.data.mapper.entity.toPublicDomain
import eu.vitamoments.app.data.models.domain.media.MediaAsset
import eu.vitamoments.app.data.models.domain.user.AccountUser
import eu.vitamoments.app.data.models.domain.user.User
import eu.vitamoments.app.data.models.enums.MediaPurposeType
import eu.vitamoments.app.data.models.enums.MediaReferenceType
import eu.vitamoments.app.dbHelpers.dbQuery
import eu.vitamoments.app.dbHelpers.kotlinUuid
import eu.vitamoments.app.dbHelpers.queries.findFriendshipByPair
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

class JVMUserRepository(
    val mediaRepository: MediaRepository
) : UserRepository {
    override suspend fun getUser(currentUserId: Uuid, userId: Uuid): RepositoryResult<User> = dbQuery {
        val entity = UserEntity.findById(userId.toJavaUuid())
            ?: return@dbQuery RepositoryResult.Error(RepositoryError.NotFound("User with id: $userId not found"))

        if (currentUserId == userId) {
            return@dbQuery RepositoryResult.Success(entity.toAccountDomain())
        }

        val friends = findFriendshipByPair(currentUserId.toJavaUuid(), userId.toJavaUuid())
        val profileImageAsset = getUserProfileMedia(entity.kotlinUuid)
        val coverImageAsset = getUserCoverMedia(entity.kotlinUuid)
        RepositoryResult.Success(if (friends?.status == FriendshipStatus.ACCEPTED) entity.toPrivateDomain(
            profileImageAsset = profileImageAsset,
            coverImageAsset = coverImageAsset)
        else entity.toPublicDomain(
            profileImageAsset = profileImageAsset,
            coverImageAsset = coverImageAsset)
        )
    }

    override suspend fun getMyAccount(userId: Uuid): RepositoryResult<AccountUser> = dbQuery {
        val entity = UserEntity.findById(userId.toJavaUuid()) ?: return@dbQuery RepositoryResult.Error(RepositoryError.NotFound("User with id: $userId not found"))
        val profileImageAsset = getUserProfileMedia(entity.kotlinUuid)
        val coverImageAsset = getUserCoverMedia(entity.kotlinUuid)
        RepositoryResult.Success(entity.toAccountDomain(
            profileImageAsset = profileImageAsset,
            coverImageAsset = coverImageAsset
        ))
    }

    override suspend fun updateMyAccount(): RepositoryResult<AccountUser> {
        TODO("Not yet implemented")
    }

    override suspend fun updateMyProfileImage(userId: Uuid, url: String): RepositoryResult<AccountUser> = dbQuery {
        throw Exception("Gebruik media service om profiel fotos te uploaden")
    }

    suspend fun getUserProfileMedia(userId: Uuid): MediaAsset? =
        when (
            val result = mediaRepository.findProfileImage(userId)
        ) {
            is RepositoryResult.Error -> null
            is RepositoryResult.Success -> result.body
        }

    suspend fun getUserCoverMedia(userId: Uuid): MediaAsset? =
        when (
            val result = mediaRepository.findAllByReferenceAndPurpose(
                referenceId = userId,
                referenceType = MediaReferenceType.USER,
                purpose = MediaPurposeType.COVER
            )
        ) {
            is RepositoryResult.Error -> null
            is RepositoryResult.Success -> result.body.firstOrNull()
        }
}