package nl.fbdevelopment.healthyplatform.data.tables

import kotlinx.datetime.LocalDateTime
import nl.fbdevelopment.healthyplatform.data.mapper.extension_functions.nowUtc
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.datetime.datetime

object RefreshTokensTable: UUIDTable("refresh_tokens") {
    val refreshToken = varchar("refresh_token", 255).uniqueIndex()
    val userId = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE)
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.nowUtc() }
    val expiredAt = datetime("expired_at")
    val revokedAt = datetime("revoked_at").nullable()
}