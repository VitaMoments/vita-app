package eu.vitamoments.app.dbHelpers.queries

import eu.vitamoments.app.data.enums.FriendshipStatus
import eu.vitamoments.app.data.records.FriendshipRecord
import eu.vitamoments.app.data.tables.FriendshipsTable
import eu.vitamoments.app.data.tables.UsersTable
import eu.vitamoments.app.dbHelpers.canonicalPair
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Exists
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.exists
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import java.util.UUID
import kotlin.enums.EnumEntries
import kotlin.uuid.Uuid

fun findFriendshipByUuid(userId: Uuid, target: Uuid): FriendshipRecord? =
    canonicalPair(userId, target).let { (a, b) -> findFriendshipByPair(a, b) }

fun findFriendshipByPair(pairA: UUID, pairB: UUID): FriendshipRecord? =
    FriendshipsTable
        .selectAll()
        .where { (FriendshipsTable.pairA eq pairA) and (FriendshipsTable.pairB eq pairB)}
        .singleOrNull()
        ?.let(FriendshipRecord::fromRow)

fun hasFriendshipExpr(
    me: UUID,
    userIdExpr: Column<EntityID<UUID>>,
    activeStatuses: Iterable<FriendshipStatus> = FriendshipStatus.entries,
): Op<Boolean> {
    val meRef = EntityID(me, UsersTable)

    val existsQuery =
        FriendshipsTable
            .select(FriendshipsTable.id)
            .where {
                (
                    ((FriendshipsTable.pairA eq meRef) and (FriendshipsTable.pairB eq userIdExpr)) or
                            ((FriendshipsTable.pairA eq userIdExpr) and (FriendshipsTable.pairB eq meRef))
                    ) and
                    (FriendshipsTable.status inList activeStatuses)
            }
            .limit(1)
    return exists(existsQuery)
}

fun getAcceptedFriendIds(viewerUuid: UUID): List<UUID> {
    val rows = FriendshipsTable
        .select(FriendshipsTable.pairA, FriendshipsTable.pairB)
        .where {
            (FriendshipsTable.status eq FriendshipStatus.ACCEPTED) and
                    ((FriendshipsTable.pairA eq viewerUuid) or (FriendshipsTable.pairB eq viewerUuid))
        }

    return rows.map { row ->
        val a = row[FriendshipsTable.pairA].value
        val b = row[FriendshipsTable.pairB].value
        if (a == viewerUuid) b else a
    }
}