package eu.vitamoments.app.data.entities

import eu.vitamoments.app.data.tables.AddressesTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.util.UUID

class AddressEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<AddressEntity>(AddressesTable)

    var ownerType by AddressesTable.ownerType
    var ownerId by AddressesTable.ownerId

    var type by AddressesTable.type
    var label by AddressesTable.label
    var isPrimary by AddressesTable.isPrimary
    var privacy by AddressesTable.privacy

    var line1 by AddressesTable.line1
    var line2 by AddressesTable.line2
    var postalCode by AddressesTable.postalCode
    var city by AddressesTable.city
    var region by AddressesTable.region
    var countryCode by AddressesTable.countryCode

    var createdAt by AddressesTable.createdAt
    var updatedAt by AddressesTable.updatedAt
    var deletedAt by AddressesTable.deletedAt
}