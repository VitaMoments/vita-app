package eu.vitamoments.app.data.entities

import eu.vitamoments.app.data.tables.FriendshipsTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import java.util.UUID

class FriendshipEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object Companion: UUIDEntityClass<FriendshipEntity>(FriendshipsTable)
    var requesterId by FriendshipsTable.requesterId
    var requester by UserEntity referencedOn FriendshipsTable.requesterId

    var receiverId by FriendshipsTable.receiverId
    var receiver by UserEntity referencedOn FriendshipsTable.receiverId

    var status by FriendshipsTable.status

    var createdAt by FriendshipsTable.createdAt
    var updatedAt by FriendshipsTable.updatedAt
    var deletedAt by FriendshipsTable.deletedAt
    var deletedById by FriendshipsTable.deletedBy
    var deletedBy by UserEntity optionalReferencedOn FriendshipsTable.deletedBy
}