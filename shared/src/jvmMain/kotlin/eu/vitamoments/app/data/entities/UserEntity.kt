package eu.vitamoments.app.data.entities

import eu.vitamoments.app.data.tables.UsersTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import java.util.UUID

class UserEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserEntity>(UsersTable)

    var username by UsersTable.username
    var email by UsersTable.email
    var alias by UsersTable.alias
    var bio by UsersTable.bio
    var role by UsersTable.role
    var password by UsersTable.password
    var createdAt by UsersTable.createdAt
    var updatedAt by UsersTable.updatedAt
    var deletedAt by UsersTable.deletedAt
    var imageUrl by UsersTable.imageUrl



}

