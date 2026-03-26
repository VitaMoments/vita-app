package eu.vitamoments.app.data.models.enums

import kotlinx.serialization.Serializable

@Serializable
enum class MediaReferenceType {
    USER,
    FEED_ITEM,
    POST,
    COMMENT
}