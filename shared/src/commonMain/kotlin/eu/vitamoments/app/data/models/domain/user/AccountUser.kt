@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package eu.vitamoments.app.data.models.domain.user

import eu.vitamoments.app.data.enums.UserRole
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * AccountUser is provided when the signed in user is this user.
 */
data class AccountUser(
    override val uuid: Uuid,
    val username: String,
    override val alias: String? = null,
    override val bio: String? = null,
    val role: UserRole = UserRole.USER,
    val email: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Instant? = null,
    override var imageUrl: String? = null
) : User
