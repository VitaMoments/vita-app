package eu.vitamoments.app.data.models.domain.address

import eu.vitamoments.app.data.models.enums.AddressType
import eu.vitamoments.app.data.models.enums.PrivacyStatus
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class Address(
    val id: Uuid,

    val type: AddressType,
    val label: String? = null,
    val isPrimary: Boolean = false,
    val privacy: PrivacyStatus,

    val line1: String,
    val line2: String? = null,
    val postalCode: String? = null,
    val city: String? = null,
    val region: String? = null,
    val countryCode: String,
)
