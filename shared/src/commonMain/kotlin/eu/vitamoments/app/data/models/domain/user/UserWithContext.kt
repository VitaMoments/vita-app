package eu.vitamoments.app.data.models.domain.user

data class UserWithContext(
    val user: User,
    val friendship: Friendship? = null
) : User by user
