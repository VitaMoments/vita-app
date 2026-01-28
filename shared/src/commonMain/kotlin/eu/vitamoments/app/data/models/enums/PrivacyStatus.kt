package eu.vitamoments.app.data.models.enums

import kotlinx.serialization.Serializable

@Serializable
enum class PrivacyStatus {
    OPEN,
    FRIENDS_ONLY,
    PRIVATE
}