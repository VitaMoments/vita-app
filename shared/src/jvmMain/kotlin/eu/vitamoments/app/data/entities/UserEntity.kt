package eu.vitamoments.app.data.entities

import eu.vitamoments.app.data.tables.UsersTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.util.UUID

class UserEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserEntity>(UsersTable)

    // Auth
    var email by UsersTable.email
    var username by UsersTable.username
    var password by UsersTable.password
    var role by UsersTable.role
    var emailVerifiedAt by UsersTable.emailVerifiedAt

    // Profile
    var firstname by UsersTable.firstname
    var lastname by UsersTable.lastname
    var alias by UsersTable.alias
    var bio by UsersTable.bio
    var phone by UsersTable.phone
    var birthDate by UsersTable.birthDate
    var imageUrl by UsersTable.imageUrl
    var coverImageUrl by UsersTable.coverImageUrl

    // Localization
    var locale by UsersTable.locale
    var timeZone by UsersTable.timeZone

    // Audit
    var createdAt by UsersTable.createdAt
    var updatedAt by UsersTable.updatedAt
    var deletedAt by UsersTable.deletedAt

//    privacy
    var detailsPrivacy by UsersTable.detailsPrivacy
}

