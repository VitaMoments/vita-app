package nl.fbdevelopment.healthyplatform.data.enums

import kotlinx.serialization.Serializable

@Serializable
enum class AuthTokenType {
    JWT, REFRESH
}