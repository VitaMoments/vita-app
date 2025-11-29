package nl.fbdevelopment.healthyplatform.data.tables

import kotlinx.datetime.LocalDateTime
import nl.fbdevelopment.healthyplatform.data.mapper.extension_functions.nowUtc
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.datetime.datetime

object UsersTable : UUIDTable("users") {
    val email = varchar("email", 150).uniqueIndex()
    val password = varchar("password", 255)
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.nowUtc() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.nowUtc() }
    val deletedAt = datetime("deleted_at").nullable()
    val imageUrl = varchar("image_url", 255).nullable()
}