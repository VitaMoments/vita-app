@file:OptIn(ExperimentalUuidApi::class)

package eu.vitamoments.app.data.mapper.enitity

import eu.vitamoments.app.data.entities.UserEntity
import eu.vitamoments.app.data.models.domain.user.PublicUser
import eu.vitamoments.app.data.models.domain.user.User
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.toKotlinUuid

fun UserEntity.toDomain() : User = User(
    uuid = this.id.value.toKotlinUuid(),
    email = this.email,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    deletedAt = this.deletedAt,
    imageUrl = this.imageUrl
)

fun UserEntity.toPublicDomain() : PublicUser = PublicUser(
    uuid = this.id.value.toKotlinUuid(),
    email = this.email,
    imageUrl = this.imageUrl
)