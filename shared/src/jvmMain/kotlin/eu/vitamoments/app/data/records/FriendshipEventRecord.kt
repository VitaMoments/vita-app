package eu.vitamoments.app.data.records

import eu.vitamoments.app.data.enums.FriendInviteEventType
import eu.vitamoments.app.data.mapper.extension_functions.toInstant
import eu.vitamoments.app.data.tables.FriendshipEventTable
import org.jetbrains.exposed.v1.core.ResultRow
import java.util.UUID
import kotlin.time.Instant

private fun eventTypeOrFallback(name: String, fallback: FriendInviteEventType): FriendInviteEventType =
    enumValues<FriendInviteEventType>().firstOrNull { it.name == name } ?: fallback

data class FriendshipEventRecord(
    val id: UUID,
    val fromUserId: UUID,
    val toUserId: UUID,
    val pairA: UUID,
    val pairB: UUID,
    val eventType: FriendInviteEventType,
    val friendshipId: UUID?,
    val meta: String?,
    val createdAt: Instant,
) {
    companion object {
        fun fromRow(row: ResultRow) = FriendshipEventRecord(
            id = row[FriendshipEventTable.id].value,
            fromUserId = row[FriendshipEventTable.fromUserId].value,
            toUserId = row[FriendshipEventTable.toUserId].value,
            pairA = row[FriendshipEventTable.pairA].value,
            pairB = row[FriendshipEventTable.pairB].value,
            eventType = row[FriendshipEventTable.eventType],
            friendshipId = row[FriendshipEventTable.friendshipId]?.value,
            meta = row[FriendshipEventTable.meta],
            createdAt = row[FriendshipEventTable.createdAt].toInstant(),
        )
    }
}
