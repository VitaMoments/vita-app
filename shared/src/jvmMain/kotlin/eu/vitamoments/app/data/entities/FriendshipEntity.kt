package eu.vitamoments.app.data.entities

import eu.vitamoments.app.data.tables.FriendshipsTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import java.util.UUID

class FriendshipEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object Companion: UUIDEntityClass<FriendshipEntity>(FriendshipsTable)
    var fromUserId by FriendshipsTable.fromUserId
    var toUserId by FriendshipsTable.toUserId
    var pairA by UserEntity referencedOn FriendshipsTable.pairA
    var pairB by UserEntity referencedOn FriendshipsTable.pairB
    var status by FriendshipsTable.status
    var createdAt by FriendshipsTable.createdAt
    var updatedAt by FriendshipsTable.updatedAt
}