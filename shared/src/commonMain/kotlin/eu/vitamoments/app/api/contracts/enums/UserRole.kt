package eu.vitamoments.app.api.contracts.enums

import kotlinx.serialization.Serializable

@Serializable
enum class UserRole {
    USER, MODERATOR, ADMIN
}