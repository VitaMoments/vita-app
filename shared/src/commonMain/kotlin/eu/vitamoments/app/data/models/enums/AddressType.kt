package eu.vitamoments.app.data.models.enums

import kotlinx.serialization.Serializable

@Serializable
enum class AddressType {
    DEFAULT, SHIPPING, BILLING, OTHER
}