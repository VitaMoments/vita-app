package eu.vitamoments.app.data.models.enums

import kotlinx.serialization.Serializable

@Serializable
enum class MediaPurposeType {
    PROFILE,
    POST,
    FEED,
    COVER
}