@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package eu.vitamoments.app.data.mapper.enitity

import eu.vitamoments.app.data.entities.UserEntity
import eu.vitamoments.app.data.mapper.extension_functions.toInstant
import eu.vitamoments.app.data.models.domain.user.AccountUser
import eu.vitamoments.app.data.models.domain.user.PrivateUser
import eu.vitamoments.app.data.models.domain.user.PublicUser
import eu.vitamoments.app.data.models.domain.user.User
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.toKotlinUuid

fun UserEntity.toPrivateDomain() : PrivateUser = PrivateUser(
    uuid = this.id.value.toKotlinUuid(),
    username = this.username,
    email = this.email,
    alias = this.alias,
    bio = this.bio,
    role = this.role,
    imageUrl = this.imageUrl
)

fun UserEntity.toPublicDomain() : PublicUser = PublicUser(
    uuid = this.id.value.toKotlinUuid(),
    alias = this.alias ?: this.username,
    bio = this.bio,
    imageUrl = this.imageUrl
)

fun UserEntity.toAccountDomain() : AccountUser = AccountUser(
    uuid = this.id.value.toKotlinUuid(),
    username = this.username,
    alias = this.alias,
    bio = this.bio,
    role = this.role,
    email = this.email,
    createdAt = this.createdAt.toInstant(),
    updatedAt = this.updatedAt.toInstant(),
    deletedAt = this.deletedAt?.toInstant(),
    imageUrl = this.imageUrl
)