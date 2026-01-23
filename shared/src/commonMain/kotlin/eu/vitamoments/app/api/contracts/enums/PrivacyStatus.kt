package eu.vitamoments.app.api.contracts.enums

import kotlinx.serialization.Serializable

@Serializable
enum class PrivacyStatus {
    OPEN,
    FRIENDS_ONLY,
    PRIVATE
}