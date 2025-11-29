@file:OptIn(ExperimentalUuidApi::class)

package nl.fbdevelopment.healthyplatform.data.mapper.enitity

import nl.fbdevelopment.healthyplatform.data.entities.UserEntity
import nl.fbdevelopment.healthyplatform.data.models.domain.user.PublicUser
import nl.fbdevelopment.healthyplatform.data.models.domain.user.User
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