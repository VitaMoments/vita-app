package eu.vitamoments.app.data.tables

import eu.vitamoments.app.data.enums.FriendshipStatus
import eu.vitamoments.app.data.mapper.extension_functions.nowUtc
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.datetime.datetime
object FriendshipsTable : UUIDTable("friendships") {
    val fromUserId = reference("from_user_id", UsersTable, onDelete = ReferenceOption.CASCADE)
    val toUserId = reference("to_user_id", UsersTable, onDelete = ReferenceOption.CASCADE)

    val pairA = reference("pair_a", UsersTable, onDelete = ReferenceOption.CASCADE)
    val pairB = reference("pair_b", UsersTable, onDelete = ReferenceOption.CASCADE)

    val status = enumerationByName<FriendshipStatus>("status", 16).default(FriendshipStatus.PENDING)

    val createdAt = datetime("created_at").clientDefault { LocalDateTime.nowUtc() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.nowUtc() }

    init {
        uniqueIndex(pairA, pairB)
        index(false, fromUserId)
        index(false, toUserId)
        index(false, status)
    }
}