package eu.vitamoments.app.data.entities

import eu.vitamoments.app.data.tables.RefreshTokensTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import java.util.UUID

class RefreshTokenEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object Companion : UUIDEntityClass<RefreshTokenEntity>(RefreshTokensTable)

    var refreshToken by RefreshTokensTable.refreshToken

    var user by UserEntity referencedOn RefreshTokensTable.userId

    var createdAt by RefreshTokensTable.createdAt
    var expiredAt by RefreshTokensTable.expiredAt
    var revokedAt by RefreshTokensTable.revokedAt
}