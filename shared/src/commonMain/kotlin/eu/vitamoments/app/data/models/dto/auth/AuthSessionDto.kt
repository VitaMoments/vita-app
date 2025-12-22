package eu.vitamoments.app.data.models.dto.auth

import eu.vitamoments.app.data.models.dto.user.AccountUserDto
import kotlinx.serialization.Serializable
import eu.vitamoments.app.data.models.dto.user.PrivateUserDto

@Serializable
data class AuthSessionDto(
    val user: AccountUserDto
)
