package eu.vitamoments.app.data.mapper.entity

import eu.vitamoments.app.data.entities.UserEntity
import eu.vitamoments.app.data.models.enums.FriendshipStatus
import eu.vitamoments.app.data.mapper.extension_functions.toInstant
import eu.vitamoments.app.data.mapper.extension_functions.toInstantStartOfDay
import eu.vitamoments.app.data.models.domain.media.MediaAsset
import eu.vitamoments.app.data.models.domain.user.AccountUser
import eu.vitamoments.app.data.models.domain.user.PrivateUser
import eu.vitamoments.app.data.models.domain.user.PublicUser
import eu.vitamoments.app.data.models.domain.user.User
import eu.vitamoments.app.data.tables.UsersTable
import eu.vitamoments.app.dbHelpers.createdAt
import eu.vitamoments.app.dbHelpers.deletedAt
import eu.vitamoments.app.dbHelpers.instantOrNull
import eu.vitamoments.app.dbHelpers.updatedAt
import org.jetbrains.exposed.v1.core.ResultRow
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid

fun UserEntity.toDomain(viewerUuid: Uuid): User = toDomainForViewer(viewerUuid)

fun UserEntity.toDomainForViewer(
    viewerUuid: Uuid,
    status: FriendshipStatus? = null,
    profileImageAsset: MediaAsset? = null,
    coverImageAsset: MediaAsset? = null
    ) : User {
    val authorUuid = this.uuid()
    return when {
        authorUuid == viewerUuid -> this.toAccountDomain(coverImageAsset = coverImageAsset, profileImageAsset = profileImageAsset)
        status == FriendshipStatus.ACCEPTED -> this.toPrivateDomain(coverImageAsset = coverImageAsset, profileImageAsset = profileImageAsset)
        else -> this.toPublicDomain(coverImageAsset = coverImageAsset, profileImageAsset = profileImageAsset)
    }
}

fun UserEntity.toPrivateDomain(
    profileImageAsset: MediaAsset? = null,
    coverImageAsset: MediaAsset? = null
) : PrivateUser = PrivateUser(
    uuid = this.uuid(),
    username = this.username,
    displayName = this.displayName(),
    bio = this.bio,
    role = this.role,
    profileImageAsset = profileImageAsset,
    phone = this.phone,
    birthDate = this.birthDate?.toInstantStartOfDay(),
    coverImageAsset = coverImageAsset,
    privacyDetails = this.detailsPrivacy
)
fun UserEntity.toPublicDomain(
    profileImageAsset: MediaAsset? = null,
    coverImageAsset: MediaAsset? = null
) : PublicUser = PublicUser(
    uuid = this.uuid(),
    displayName = this.displayName(),
    bio = this.bio,
    profileImageAsset = profileImageAsset,
    coverImageAsset = coverImageAsset
)

fun UserEntity.toAccountDomain(
    profileImageAsset: MediaAsset? = null,
    coverImageAsset: MediaAsset? = null
) : AccountUser = AccountUser(
    uuid = this.uuid(),
    username = this.username,
    alias = this.alias,
    bio = this.bio,
    role = this.role,
    email = this.email,
    createdAt = this.createdAt.toInstant(),
    updatedAt = this.updatedAt.toInstant(),
    deletedAt = this.deletedAt?.toInstant(),
    profileImageAsset = profileImageAsset,
    birthDate = this.birthDate?.toInstantStartOfDay(),
    firstname = this.firstname,
    lastname = this.lastname,
    phone = this.phone,
    coverImageAsset = coverImageAsset,
    locale = this.locale,
    timeZone = this.timeZone,
    privacyDetails = this.detailsPrivacy
    )

fun ResultRow.rowToPublicResult(
    profileImageAsset: MediaAsset? = null,
    coverImageAsset: MediaAsset? = null
) : PublicUser = PublicUser(
    uuid = this.userUuid(),
    displayName = this.displayName(),
    bio = this[UsersTable.bio],
    profileImageAsset = profileImageAsset,
    coverImageAsset = coverImageAsset
)
fun ResultRow.rowToPrivateResult(
    profileImageAsset: MediaAsset? = null,
    coverImageAsset: MediaAsset? = null
) : PrivateUser = PrivateUser(
    uuid = this.userUuid(),
    username = this[UsersTable.username],
    displayName = this.displayName(),
    bio = this[UsersTable.bio],
    role = this[UsersTable.role],
    birthDate = this[UsersTable.birthDate]?.toInstantStartOfDay(),
    profileImageAsset = profileImageAsset,
    coverImageAsset = coverImageAsset
)
fun ResultRow.rowToAccountResult(
    profileImageAsset: MediaAsset? = null,
    coverImageAsset: MediaAsset? = null
) : AccountUser = AccountUser(
    uuid = this.userUuid(),
    username = this[UsersTable.username],
    alias = this.displayName(),
    bio = this[UsersTable.bio],
    role = this[UsersTable.role],
    email = this[UsersTable.email],
    createdAt = this.createdAt(UsersTable),
    updatedAt = this.updatedAt(UsersTable),
    deletedAt = this.deletedAt(UsersTable),
    birthDate = this.instantOrNull(UsersTable.birthDate, startOfDay = true),
    firstname = this[UsersTable.firstname],
    lastname = this[UsersTable.lastname],
    phone = this[UsersTable.phone],
    privacyDetails = this[UsersTable.detailsPrivacy],
    locale = this[UsersTable.locale],
    timeZone = this[UsersTable.timeZone],
    profileImageAsset = profileImageAsset,
    coverImageAsset = coverImageAsset
)

private fun ResultRow.userUuid() =
    this[UsersTable.id].value.toKotlinUuid()

private fun ResultRow.displayName(): String =
    this[UsersTable.alias] ?: this[UsersTable.username]

private fun UserEntity.displayName(): String =
    this.alias ?: this.username

private fun UserEntity.uuid() =
    this.id.value.toKotlinUuid()