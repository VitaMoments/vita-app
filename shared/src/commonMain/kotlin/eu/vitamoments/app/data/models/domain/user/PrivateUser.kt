@file:OptIn(ExperimentalUuidApi::class)

package eu.vitamoments.app.data.models.domain.user

import eu.vitamoments.app.data.enums.UserRole
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Private User is the user provided to friends etc.
 */
data class PrivateUser(
    override val uuid: Uuid,
    val username: String,
    override val alias: String? = null,
    override val bio: String? = null,
    val role: UserRole = UserRole.USER,
    val email: String,
    override var imageUrl: String? = null
) : User
