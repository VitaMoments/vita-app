package eu.vitamoments.app.data.models.domain.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("CONTEXT")
data class UserWithContext(
    val user: User,
    val friendship: Friendship? = null
) : User by user
