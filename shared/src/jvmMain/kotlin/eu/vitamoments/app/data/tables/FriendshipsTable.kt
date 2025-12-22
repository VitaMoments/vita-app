package eu.vitamoments.app.data.tables

import eu.vitamoments.app.data.enums.FriendshipStatus
import eu.vitamoments.app.data.mapper.extension_functions.nowUtc
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.datetime.datetime

object FriendshipsTable : UUIDTable("friendships") {
    val requesterId = reference("requester_id", UsersTable, onDelete = ReferenceOption.CASCADE)
    val receiverId = reference("receiver_id", UsersTable, onDelete = ReferenceOption.CASCADE)
    val status = enumerationByName<FriendshipStatus>("status", 16).default(FriendshipStatus.PENDING)

    val createdAt = datetime("created_at").clientDefault { LocalDateTime.nowUtc() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.nowUtc() }
    val deletedAt = datetime("deleted_at").nullable()
    val deletedBy = reference("deleted_by", UsersTable, onDelete = ReferenceOption.CASCADE).nullable()

    init {
        uniqueIndex(requesterId, receiverId)
    }
}