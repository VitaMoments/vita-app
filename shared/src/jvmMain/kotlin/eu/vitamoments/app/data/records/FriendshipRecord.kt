package eu.vitamoments.app.data.records

import eu.vitamoments.app.data.mapper.extension_functions.toInstant
import eu.vitamoments.app.data.models.enums.FriendshipStatus
import eu.vitamoments.app.data.tables.FriendshipsTable
import org.jetbrains.exposed.v1.core.ResultRow
import java.util.UUID
import kotlin.time.Instant

data class FriendshipRecord(
    val id: UUID,
    val fromUserId: UUID,
    val toUserId: UUID,
    val status: FriendshipStatus,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    companion object {
        fun fromRow(row: ResultRow) = FriendshipRecord(
            id = row[FriendshipsTable.id].value,
            fromUserId = row[FriendshipsTable.fromUserId].value,
            toUserId = row[FriendshipsTable.toUserId].value,
            status = row[FriendshipsTable.status],
            createdAt = row[FriendshipsTable.createdAt].toInstant(),
            updatedAt = row[FriendshipsTable.updatedAt].toInstant(),
        )
    }
}
