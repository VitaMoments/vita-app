package eu.vitamoments.app.data.models.domain.location

import kotlin.uuid.Uuid

data class Location(
    val uuid: Uuid,
    val countryCode: String,
    val city: String,
    val street: String? = null,
    val houseNumber: String? = null,
    val postalCode: String? = null,
    val region: String? = null
)
