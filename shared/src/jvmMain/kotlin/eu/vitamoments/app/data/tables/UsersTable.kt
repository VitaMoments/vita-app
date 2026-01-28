package eu.vitamoments.app.data.tables

import eu.vitamoments.app.data.models.enums.UserRole
import kotlinx.datetime.LocalDateTime
import eu.vitamoments.app.data.mapper.extension_functions.nowUtc
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.datetime.datetime

object UsersTable : UUIDTable("users") {
    val email = varchar("email", 150).uniqueIndex()
    val username = varchar("username", 100).uniqueIndex()
    val alias = varchar("alias", 100).nullable()
    val bio = varchar("bio", 255).nullable()
    val role = enumerationByName<UserRole>("role", 10).default(UserRole.USER)
    val password = varchar("password", 255)
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.nowUtc() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.nowUtc() }
    val deletedAt = datetime("deleted_at").nullable()
    val imageUrl = varchar("image_url", 255).nullable()
}