package eu.vitamoments.app.data.tables

import eu.vitamoments.app.data.models.enums.FriendInviteEventType
import eu.vitamoments.app.data.mapper.extension_functions.nowUtc
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.datetime.datetime

object FriendshipEventTable : UUIDTable("friendship_events") {
    val fromUserId = reference("from_user_id", UsersTable, onDelete = ReferenceOption.CASCADE)
    val toUserId = reference("to_user_id", UsersTable, onDelete = ReferenceOption.CASCADE)
    val pairA = reference("pair_a", UsersTable, onDelete = ReferenceOption.CASCADE)
    val pairB = reference("pair_b", UsersTable, onDelete = ReferenceOption.CASCADE)
    val eventType = enumerationByName<FriendInviteEventType>("event_type", 32)
    val friendshipId = reference("friendship_id", FriendshipsTable, onDelete = ReferenceOption.SET_NULL).nullable()
    val meta = text("meta").nullable()
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.nowUtc() }

    init {
        index(false, pairA, pairB, eventType, createdAt)
    }
}