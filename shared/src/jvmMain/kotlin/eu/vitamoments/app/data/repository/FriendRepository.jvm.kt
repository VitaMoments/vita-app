package eu.vitamoments.app.data.repository

import eu.vitamoments.app.data.entities.FriendshipEntity
import eu.vitamoments.app.data.entities.UserEntity
import eu.vitamoments.app.data.enums.FriendInviteEventType
import eu.vitamoments.app.data.enums.FriendshipStatus
import eu.vitamoments.app.data.mapper.entity.rowToPrivateResult
import eu.vitamoments.app.data.mapper.entity.rowToPublicResult
import eu.vitamoments.app.data.mapper.entity.toDomain
import eu.vitamoments.app.data.mapper.extension_functions.minus
import eu.vitamoments.app.data.mapper.extension_functions.nowUtc
import eu.vitamoments.app.data.models.domain.user.Friendship
import eu.vitamoments.app.data.models.domain.user.PrivateUser
import eu.vitamoments.app.data.models.domain.user.PublicUser
import eu.vitamoments.app.data.tables.FriendshipEventTable
import eu.vitamoments.app.data.tables.FriendshipsTable
import eu.vitamoments.app.data.tables.UsersTable
import eu.vitamoments.app.dbHelpers.canonicalPair
import eu.vitamoments.app.dbHelpers.dbQuery
import eu.vitamoments.app.dbHelpers.queries.findFriendshipByPair
import eu.vitamoments.app.dbHelpers.queries.hasFriendshipExpr
import eu.vitamoments.app.dbHelpers.queries.searchPredicate
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greaterEq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.inSubQuery
import org.jetbrains.exposed.v1.core.isNull
import org.jetbrains.exposed.v1.core.neq
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import java.util.UUID
import kotlin.time.Duration.Companion.days
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

class JVMFriendRepository() : FriendRepository {
    override suspend fun searchNewFriends(
        userId: Uuid,
        query: String?,
        limit: Int,
        offset: Int
    ): RepositoryResponse<List<PublicUser>> = dbQuery {
        val me = userId.toJavaUuid()
        val needle = query?.trim()?.takeIf { it.isNotBlank() }
        val safeLimit = limit.coerceIn(1, 50)
        val safeOffset = offset.coerceAtLeast(0).toLong()

        val hasFriendship = hasFriendshipExpr(me, UsersTable.id, FriendshipStatus.activeEntries)

        val whereExpr = UsersTable.searchPredicate(me, needle) and (hasFriendship neq Op.TRUE)
        val result = UsersTable
            .selectAll()
            .where { whereExpr }
            .orderBy(UsersTable.username to SortOrder.ASC)
            .limit(safeLimit)
            .offset(safeOffset)
            .toList()
            .map { it.rowToPublicResult() }
        println(result)
        RepositoryResponse.Success(result)
    }

    override suspend fun searchFriends(
        userId: Uuid,
        query: String?,
        limit: Int,
        offset: Int
    ): RepositoryResponse<List<PrivateUser>> = dbQuery {
        val me = userId.toJavaUuid()
        val needle = query?.trim()?.takeIf { it.isNotBlank() }
        val safeLimit = limit.coerceIn(1, 50)
        val safeOffset = offset.coerceAtLeast(0).toLong()

        val hasFriendship = hasFriendshipExpr(me, UsersTable.id, listOf(FriendshipStatus.ACCEPTED))

        val whereExpr = UsersTable.searchPredicate(me, needle) and (hasFriendship eq Op.TRUE)

        val result = UsersTable
            .selectAll()
            .where { whereExpr }
            .orderBy(UsersTable.username to SortOrder.ASC)
            .limit(safeLimit)
            .offset(safeOffset)
            .toList()
            .map { it.rowToPrivateResult() }
        RepositoryResponse.Success(result)
    }

    override suspend fun incomingRequests(userId: Uuid): RepositoryResponse<List<PublicUser>> = dbQuery {
        val me = userId.toJavaUuid()
        val meRef = EntityID(me, UsersTable)

        val fromIdsSubQuery = FriendshipsTable.select(FriendshipsTable.fromUserId)
            .where { (FriendshipsTable.toUserId eq meRef) and(FriendshipsTable.status eq FriendshipStatus.PENDING) }

        val result = UsersTable
            .selectAll()
            .where {
                UsersTable.deletedAt.isNull() and
                        (UsersTable.id inSubQuery fromIdsSubQuery)
            }
            .orderBy(UsersTable.username to SortOrder.ASC)
            .toList()
            .map { it.rowToPublicResult() }

        RepositoryResponse.Success(result)
    }

    override suspend fun outgoingRequests(userId: Uuid): RepositoryResponse<List<PublicUser>> = dbQuery {
        val me = userId.toJavaUuid()
        val meRef = EntityID(me, UsersTable)

        val fromIdsSubQuery = FriendshipsTable.select(FriendshipsTable.toUserId)
            .where { (FriendshipsTable.fromUserId eq meRef) and(FriendshipsTable.status eq FriendshipStatus.PENDING) }

        val result = UsersTable
            .selectAll()
            .where {
                UsersTable.deletedAt.isNull() and
                        (UsersTable.id inSubQuery fromIdsSubQuery)
            }
            .orderBy(UsersTable.username to SortOrder.ASC)
            .toList()
            .map { it.rowToPublicResult() }

        RepositoryResponse.Success(result)
    }

    override suspend fun invite(
        userId: Uuid,
        otherId: Uuid
    ): RepositoryResponse<Friendship> = dbQuery{
        if (userId == otherId) return@dbQuery RepositoryResponse.Error.InvalidData("id", "You cannot invite yourself")

        val me = userId.toJavaUuid()
        val other = otherId.toJavaUuid()
        val now = LocalDateTime.nowUtc()

        val meUser = UserEntity.findById(me) ?: return@dbQuery RepositoryResponse.Error.InvalidData("id", "User not found")
        val otherUser = UserEntity.findById(other) ?: return@dbQuery RepositoryResponse.Error.InvalidData("id", "Target friend not found")
        val (pairA, pairB) = canonicalPair(me, other)

        val cooldownSince = now.minus(1.days)
        val hasRecentDecline = FriendshipEventTable
            .select(FriendshipEventTable.id)
            .where {
                (FriendshipEventTable.pairA eq EntityID(pairA, UsersTable)) and
                        (FriendshipEventTable.pairB eq EntityID(pairB, UsersTable)) and
                        (FriendshipEventTable.eventType eq FriendInviteEventType.DECLINED) and
                        (FriendshipEventTable.createdAt greaterEq cooldownSince)
            }
            .limit(1)
            .any()

        if (hasRecentDecline) {
            FriendshipEventTable.insert {
                it[fromUserId] = EntityID(me, UsersTable)
                it[toUserId] = EntityID(other, UsersTable)
                it[FriendshipEventTable.pairA] = EntityID(pairA, UsersTable)
                it[FriendshipEventTable.pairB] = EntityID(pairB, UsersTable)
                it[eventType] = FriendInviteEventType.AUTO_REJECTED
                it[meta] = """{"reason":"cooldown_after_decline"}"""
            }
            return@dbQuery RepositoryResponse.Error.RequestLimitReached()
        }

        val existing = FriendshipsTable
            .select(
                FriendshipsTable.id,
                FriendshipsTable.status,
                FriendshipsTable.fromUserId,
                FriendshipsTable.toUserId
            )
            .where {
                (FriendshipsTable.pairA eq EntityID(pairA, UsersTable)) and
                        (FriendshipsTable.pairB eq EntityID(pairB, UsersTable))
            }
            .singleOrNull()

        val entity = if (existing != null) {
            val id = existing[FriendshipsTable.id].value
            when (existing[FriendshipsTable.status]) {
                FriendshipStatus.ACCEPTED -> return@dbQuery RepositoryResponse.Error.InvalidData("friendrequest", "Users are friends already")
                FriendshipStatus.PENDING -> return@dbQuery RepositoryResponse.Error.InvalidData("friendrequest", "Invite is already pending")
                FriendshipStatus.DECLINED, FriendshipStatus.REMOVED -> {
                    FriendshipEntity.findByIdAndUpdate(id) { e ->
                        e.status = FriendshipStatus.PENDING
                        e.fromUserId = meUser.id
                        e.toUserId = otherUser.id
                        e.updatedAt = now
                    } ?: return@dbQuery RepositoryResponse.Error.Internal()
                }
                else -> return@dbQuery RepositoryResponse.Error.Internal()
            }
        } else {
            val pairAUser = if (pairA == me) meUser else otherUser
            val pairBUser = if (pairB == me) meUser else otherUser

            FriendshipEntity.new {
                fromUserId = meUser.id
                toUserId = otherUser.id
                this.pairA = pairAUser
                this.pairB = pairBUser
                status = FriendshipStatus.PENDING
                updatedAt = now
            }
        }
        logEvent(
            actor = me,
            other = other,
            pairA = pairA,
            pairB = pairB,
            friendshipId = entity.id.value,
            preferredEventTypeName = "PENDING",
            action = "pending",
        )
        RepositoryResponse.Success(entity.toDomain(userId))
    }

    override suspend fun accept(
        userId: Uuid,
        otherId: Uuid
    ): RepositoryResponse<Friendship> = dbQuery{
        if (userId == otherId) {
            return@dbQuery RepositoryResponse.Error.InvalidData("id", "You cannot accept yourself")
        }

        val me = userId.toJavaUuid()
        val other = otherId.toJavaUuid()
        val now = LocalDateTime.nowUtc()
        val (pairA, pairB) = canonicalPair(me, other)

        val record = findFriendshipByPair(pairA, pairB)
            ?: return@dbQuery RepositoryResponse.Error.InvalidData("friendrequest", "Invite not found")

        if (record.status != FriendshipStatus.PENDING) {
            return@dbQuery RepositoryResponse.Error.InvalidData("friendrequest", "Invite is not pending")
        }
        if (record.toUserId != me) {
            return@dbQuery RepositoryResponse.Error.InvalidData("friendrequest", "Only the receiver can accept an invite")
        }

        val entity = FriendshipEntity.findByIdAndUpdate(record.id) { e ->
            e.status = FriendshipStatus.ACCEPTED
            e.updatedAt = now
        } ?: return@dbQuery RepositoryResponse.Error.Internal()

        logEvent(
            actor = me,
            other = other,
            pairA = pairA,
            pairB = pairB,
            friendshipId = entity.id.value,
            preferredEventTypeName = "ACCEPTED",
            action = "accepted",
        )

        RepositoryResponse.Success(entity.toDomain(userId))
    }

    override suspend fun delete(
        userId: Uuid,
        otherId: Uuid
    ): RepositoryResponse<Friendship> = dbQuery{
        if (userId == otherId) {
            return@dbQuery RepositoryResponse.Error.InvalidData("id", "You cannot remove yourself")
        }

        val me = userId.toJavaUuid()
        val other = otherId.toJavaUuid()
        val now = LocalDateTime.nowUtc()
        val (pairA, pairB) = canonicalPair(me, other)

        val record = findFriendshipByPair(pairA, pairB)
            ?: return@dbQuery RepositoryResponse.Error.InvalidData("friendship", "Friendship not found")

        val (newStatus, preferredEventTypeName, action, metaJson) = when (record.status) {
            FriendshipStatus.ACCEPTED ->
                Quad(FriendshipStatus.REMOVED, "REMOVED", "removed", """{"reason":"unfriended"}""")

            FriendshipStatus.PENDING -> {
                when (me) {
                    record.fromUserId ->
                        Quad(FriendshipStatus.REMOVED, "CANCELLED", "cancelled", """{"reason":"cancelled_by_sender"}""")
                    record.toUserId ->
                        // keep DECLINED so your cooldown logic stays consistent
                        Quad(FriendshipStatus.DECLINED, "DECLINED", "declined", """{"reason":"removed_pending_by_receiver"}""")
                    else ->
                        return@dbQuery RepositoryResponse.Error.Internal()
                }
            }

            FriendshipStatus.REMOVED ->
                return@dbQuery RepositoryResponse.Error.InvalidData("friendship", "Friendship already removed")

            FriendshipStatus.DECLINED ->
                return@dbQuery RepositoryResponse.Error.InvalidData("friendrequest", "Invite already declined")

            else ->
                return@dbQuery RepositoryResponse.Error.InvalidData("friendship", "Unsupported status: ${record.status}")
        }

        val entity = FriendshipEntity.findByIdAndUpdate(record.id) { e ->
            e.status = newStatus
            e.updatedAt = now
        } ?: return@dbQuery RepositoryResponse.Error.Internal()

        logEvent(
            actor = me,
            other = other,
            pairA = pairA,
            pairB = pairB,
            friendshipId = entity.id.value,
            preferredEventTypeName = preferredEventTypeName,
            action = action,
            metaJson = metaJson,
        )

        RepositoryResponse.Success(entity.toDomain(userId))
    }

    override suspend fun decline(
        userId: Uuid,
        otherId: Uuid
    ): RepositoryResponse<Friendship> = dbQuery{
        if (userId == otherId) {
            return@dbQuery RepositoryResponse.Error.InvalidData("id", "You cannot decline yourself")
        }
        val me = userId.toJavaUuid()
        val other = otherId.toJavaUuid()
        val now = LocalDateTime.nowUtc()
        val (pairA, pairB) = canonicalPair(me, other)

        val record = findFriendshipByPair(pairA, pairB)
            ?: return@dbQuery RepositoryResponse.Error.InvalidData("friendrequest", "Invite not found")

        if (record.status != FriendshipStatus.PENDING) {
            return@dbQuery RepositoryResponse.Error.InvalidData("friendrequest", "Invite is not pending")
        }
        if (record.toUserId != me) {
            return@dbQuery RepositoryResponse.Error.InvalidData("friendrequest", "Only the receiver can decline an invite")
        }

        val entity = FriendshipEntity.findByIdAndUpdate(record.id) { e ->
            e.status = FriendshipStatus.DECLINED
            e.updatedAt = now
        } ?: return@dbQuery RepositoryResponse.Error.Internal()

        logEvent(
            actor = me,
            other = other,
            pairA = pairA,
            pairB = pairB,
            friendshipId = entity.id.value,
            preferredEventTypeName = "DECLINED",
            action = "declined",
            metaJson = """{"reason":"declined_by_receiver"}""",
        )

        RepositoryResponse.Success(entity.toDomain(userId))
    }

    private fun eventTypeOrFallback(name: String, fallback: FriendInviteEventType): FriendInviteEventType =
        enumValues<FriendInviteEventType>().firstOrNull { it.name == name } ?: fallback

    private fun logEvent(
        actor: UUID,
        other: UUID,
        pairA: UUID,
        pairB: UUID,
        friendshipId: UUID?,
        preferredEventTypeName: String,
        action: String,
        metaJson: String? = null
    ) {
        val t = eventTypeOrFallback(preferredEventTypeName, FriendInviteEventType.SENT)

        val meta = when {
            metaJson == null -> """{"action":"$action"}"""
            metaJson.trim().startsWith("{") ->
                metaJson.trim().removePrefix("{").let { rest -> """{"action":"$action",$rest""" }
            else -> """{"action":"$action","meta":${metaJson.trim()}}"""
        }

        FriendshipEventTable.insert {
            it[fromUserId] = actor
            it[toUserId] = other
            it[FriendshipEventTable.pairA] = pairA
            it[FriendshipEventTable.pairB] = pairB
            it[eventType] = t
            it[FriendshipEventTable.friendshipId] = friendshipId
            it[FriendshipEventTable.meta] = meta
        }
    }

    private data class Quad<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)

    fun getStatusesByAuthors(
        viewerUuid: UUID,
        authorUuids: List<UUID>
    ): Map<UUID, FriendshipStatus> {
        val authors = authorUuids.distinct().filter { it != viewerUuid }
        if (authors.isEmpty()) return emptyMap()

        // Canonieke splits:
        // viewer < author -> (pairA=viewer, pairB=author)
        // author < viewer -> (pairA=author, pairB=viewer)
        val (greaterThanViewer, lessThanViewer) = authors.partition { it > viewerUuid }

        val rows = FriendshipsTable
            .select(FriendshipsTable.pairA, FriendshipsTable.pairB, FriendshipsTable.status)
            .where {
                ((FriendshipsTable.pairA eq viewerUuid) and (FriendshipsTable.pairB inList greaterThanViewer)) or
                        ((FriendshipsTable.pairB eq viewerUuid) and (FriendshipsTable.pairA inList lessThanViewer))
            }

        return rows.associate { row ->
            val a = row[FriendshipsTable.pairA].value
            val b = row[FriendshipsTable.pairB].value
            val other = if (a == viewerUuid) b else a
            other to row[FriendshipsTable.status]
        }
    }
}

