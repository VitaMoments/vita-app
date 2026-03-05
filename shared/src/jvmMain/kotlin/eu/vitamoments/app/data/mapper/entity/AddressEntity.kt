package eu.vitamoments.app.data.mapper.entity

import eu.vitamoments.app.data.entities.AddressEntity
import eu.vitamoments.app.data.models.domain.address.Address
import eu.vitamoments.app.dbHelpers.kotlinUuid

fun AddressEntity.toDomain(): Address =
    Address(
        id = this.kotlinUuid,
        type = this.type,
        label = this.label,
        isPrimary = this.isPrimary,
        privacy = this.privacy,
        line1 = this.line1,
        line2 = this.line2,
        postalCode = this.postalCode,
        city = this.city,
        region = this.region,
        countryCode = this.countryCode,
    )