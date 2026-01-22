package eu.vitamoments.app.data.enums

import kotlinx.serialization.Serializable

@Serializable
enum class PrivacyStatus {
    OPEN,
    FRIENDS_ONLY,
    PRIVATE
}