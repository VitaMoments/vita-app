package eu.vitamoments.app.data.tables

import eu.vitamoments.app.data.models.enums.AddressOwnerType
import eu.vitamoments.app.data.models.enums.AddressType
import eu.vitamoments.app.data.models.enums.PrivacyStatus
import eu.vitamoments.app.data.tables.base.BaseUUIDTable

object AddressesTable : BaseUUIDTable("addresses") {

    // polymorphic owner
    val ownerType = enumerationByName<AddressOwnerType>("owner_type", 24)
    val ownerId = uuid("owner_id") // no FK possible (owner can be user/company/community)

    // address metadata
    val type = enumerationByName<AddressType>("type", 16).default(AddressType.DEFAULT)
    val label = varchar("label", 80).nullable() // e.g. "HQ", "Home", etc.
    val isPrimary = bool("is_primary").default(false)
    val privacy = enumerationByName<PrivacyStatus>("privacy", 16).default(PrivacyStatus.PRIVATE)

    // address fields
    val line1 = varchar("line1", 120)
    val line2 = varchar("line2", 120).nullable()
    val postalCode = varchar("postal_code", 32).nullable()
    val city = varchar("city", 80).nullable()
    val region = varchar("region", 80).nullable()
    val countryCode = varchar("country_code", 2) // "NL"

    init {
        index(false, ownerType, ownerId)
        index(false, countryCode)
        index(false, ownerType, ownerId, type)
    }
}