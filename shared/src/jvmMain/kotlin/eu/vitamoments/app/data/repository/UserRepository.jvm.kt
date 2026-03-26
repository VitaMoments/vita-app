package eu.vitamoments.app.data.repository

import eu.vitamoments.app.data.entities.UserEntity
import eu.vitamoments.app.data.mapper.entity.toAccountDomain
import eu.vitamoments.app.data.mapper.entity.toPrivateDomain
import eu.vitamoments.app.data.mapper.entity.toPublicDomain
import eu.vitamoments.app.data.models.domain.common.PagedResult
import eu.vitamoments.app.data.models.domain.friendship.AcceptedFriendship
import eu.vitamoments.app.data.models.domain.friendship.Friendship
import eu.vitamoments.app.data.models.domain.friendship.PendingFriendship
import eu.vitamoments.app.data.models.domain.media.MediaAsset
import eu.vitamoments.app.data.models.domain.user.AccountUser
import eu.vitamoments.app.data.models.domain.user.User
import eu.vitamoments.app.data.models.domain.user.UserWithContext
import eu.vitamoments.app.data.models.enums.FriendshipDirection
import eu.vitamoments.app.data.models.enums.FriendshipStatus
import eu.vitamoments.app.data.models.enums.MediaPurposeType
import eu.vitamoments.app.data.models.enums.MediaReferenceType
import eu.vitamoments.app.data.models.requests.user_requests.UpdateMyAccountRequest
import eu.vitamoments.app.data.records.FriendshipRecord
import eu.vitamoments.app.data.tables.UsersTable
import eu.vitamoments.app.dbHelpers.dbQuery
import eu.vitamoments.app.dbHelpers.kotlinUuid
import eu.vitamoments.app.dbHelpers.queries.findFriendshipByUuid
import eu.vitamoments.app.dbHelpers.queries.searchPredicate
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.jdbc.selectAll
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

class JVMUserRepository(
    private val mediaRepository: MediaRepository
) : UserRepository {

    override suspend fun getUser(currentUserId: Uuid, userId: Uuid): RepositoryResult<UserWithContext> = dbQuery {
        val entity = UserEntity.findById(userId.toJavaUuid())
            ?: return@dbQuery RepositoryResult.Error(RepositoryError.NotFound("User with id: $userId not found"))

        val profileImageAsset = getUserProfileMedia(entity.kotlinUuid)
        val coverImageAsset = getUserCoverMedia(entity.kotlinUuid)

        if (currentUserId == userId) {
            return@dbQuery RepositoryResult.Success(
                UserWithContext(
                    user = entity.toAccountDomain(
                        profileImageAsset = profileImageAsset,
                        coverImageAsset = coverImageAsset
                    )
                )
            )
        }

        val friendshipRecord = findFriendshipByUuid(currentUserId, userId)
        val friendship = friendshipRecord?.toDomain(currentUserId, userId)

        val user: User = when (friendshipRecord?.status) {
            FriendshipStatus.ACCEPTED -> entity.toPrivateDomain(
                profileImageAsset = profileImageAsset,
                coverImageAsset = coverImageAsset
            )

            else -> entity.toPublicDomain(
                profileImageAsset = profileImageAsset,
                coverImageAsset = coverImageAsset
            )
        }

        RepositoryResult.Success(UserWithContext(user = user, friendship = friendship))
    }

    override suspend fun searchUsers(
        currentUserId: Uuid,
        query: String?,
        limit: Int,
        offset: Int
    ): RepositoryResult<PagedResult<User>> = dbQuery {
        val me = currentUserId.toJavaUuid()
        val needle = query?.trim()?.takeIf { it.isNotBlank() }
        val safeLimit = limit.coerceIn(1, 50)
        val safeOffset = offset.coerceAtLeast(0).toLong()

        val whereExpr = UsersTable.searchPredicate(
            meId = me,
            needle = needle,
            includeSelf = false,
            includeRemoved = false
        )

        val total: Long = UsersTable
            .selectAll()
            .where { whereExpr }
            .count()

        val items = UsersTable
            .selectAll()
            .where { whereExpr }
            .orderBy(UsersTable.username to SortOrder.ASC)
            .limit(safeLimit)
            .offset(safeOffset)
            .map { row ->
                val userUuid = row[UsersTable.id].value.toKotlinUuid()
                val friendshipRecord = findFriendshipByUuid(currentUserId, userUuid)
                val profileImageAsset = getUserProfileMedia(userUuid)
                val coverImageAsset = getUserCoverMedia(userUuid)
                val userEntity = UserEntity.findById(row[UsersTable.id].value)
                    ?: error("User row exists but entity was not found")

                when (friendshipRecord?.status) {
                    FriendshipStatus.ACCEPTED -> userEntity.toPrivateDomain(
                        profileImageAsset = profileImageAsset,
                        coverImageAsset = coverImageAsset
                    )

                    else -> userEntity.toPublicDomain(
                        profileImageAsset = profileImageAsset,
                        coverImageAsset = coverImageAsset
                    )
                }
            }

        val hasMore = safeOffset + safeLimit < total
        val nextOffset = if (hasMore) safeOffset + safeLimit else null

        RepositoryResult.Success(
            PagedResult(
                items = items,
                limit = safeLimit,
                offset = safeOffset,
                total = total,
                hasMore = hasMore,
                nextOffset = nextOffset
            )
        )
    }

    override suspend fun getMyAccount(userId: Uuid): RepositoryResult<AccountUser> = dbQuery {
        val entity = UserEntity.findById(userId.toJavaUuid())
            ?: return@dbQuery RepositoryResult.Error(
                RepositoryError.NotFound("User with id: $userId not found")
            )

        val profileImageAsset = getUserProfileMedia(entity.kotlinUuid)
        val coverImageAsset = getUserCoverMedia(entity.kotlinUuid)

        RepositoryResult.Success(
            entity.toAccountDomain(
                profileImageAsset = profileImageAsset,
                coverImageAsset = coverImageAsset
            )
        )
    }

    override suspend fun updateMyAccount(
        userId: Uuid,
        request: UpdateMyAccountRequest
    ): RepositoryResult<AccountUser> = dbQuery {
        val entity = UserEntity.findById(userId.toJavaUuid())
            ?: return@dbQuery RepositoryResult.Error(
                RepositoryError.NotFound("User with id: $userId not found")
            )

        val parsedBirthDate = request.birthDate
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?.let {
                runCatching { LocalDate.parse(it) }.getOrElse {
                    return@dbQuery RepositoryResult.Error(
                        RepositoryError.Validation(
                            errors = listOf(
                                RepositoryError.FieldError(
                                    "birthDate",
                                    "Invalid date format, expected yyyy-MM-dd"
                                )
                            )
                        )
                    )
                }
            }

        entity.alias = request.alias
        entity.bio = request.bio
        entity.firstname = request.firstname
        entity.lastname = request.lastname
        entity.phone = request.phone
        entity.birthDate = parsedBirthDate
        entity.locale = request.locale
        entity.timeZone = request.timeZone
        request.privacyDetails?.let { entity.detailsPrivacy = it }

        val profileImageAsset = getUserProfileMedia(entity.kotlinUuid)
        val coverImageAsset = getUserCoverMedia(entity.kotlinUuid)

        RepositoryResult.Success(
            entity.toAccountDomain(
                profileImageAsset = profileImageAsset,
                coverImageAsset = coverImageAsset
            )
        )
    }

    override suspend fun updateMyProfileImage(userId: Uuid, url: String): RepositoryResult<AccountUser> = dbQuery {
        throw Exception("Gebruik media service om profiel fotos te uploaden")
    }

    private suspend fun getUserProfileMedia(userId: Uuid): MediaAsset? =
        when (val result = mediaRepository.findProfileImage(userId)) {
            is RepositoryResult.Error -> null
            is RepositoryResult.Success -> result.body
        }

    private suspend fun getUserCoverMedia(userId: Uuid): MediaAsset? =
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

private fun FriendshipRecord.toDomain(currentUserId: Uuid, otherUserId: Uuid): Friendship? {
    val direction =
        if (fromUserId == currentUserId.toJavaUuid()) FriendshipDirection.OUTGOING
        else FriendshipDirection.INCOMING

    return when (status) {
        FriendshipStatus.PENDING -> PendingFriendship(
            uuid = id.toKotlinUuid(),
            direction = direction,
            otherUserId = otherUserId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        FriendshipStatus.ACCEPTED -> AcceptedFriendship(
            uuid = id.toKotlinUuid(),
            otherUserId = otherUserId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        else -> null
    }
}
