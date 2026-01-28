package eu.vitamoments.app.data.repository

import eu.vitamoments.app.data.entities.FriendshipEntity
import eu.vitamoments.app.data.entities.UserEntity
import eu.vitamoments.app.data.mapper.entity.rowToPrivateResult
import eu.vitamoments.app.data.mapper.entity.rowToPublicResult
import eu.vitamoments.app.data.mapper.entity.toDomain
import eu.vitamoments.app.data.mapper.extension_functions.minus
import eu.vitamoments.app.data.mapper.extension_functions.nowUtc
import eu.vitamoments.app.data.mapper.extension_functions.toInstant
import eu.vitamoments.app.data.models.domain.common.PagedResult
import eu.vitamoments.app.data.models.domain.friendship.AcceptedFriendship
import eu.vitamoments.app.data.models.domain.friendship.Friendship
import eu.vitamoments.app.data.models.domain.friendship.PendingFriendship
import eu.vitamoments.app.data.models.domain.user.PublicUser
import eu.vitamoments.app.data.models.domain.user.UserWithContext
import eu.vitamoments.app.data.models.enums.FriendInviteEventType
import eu.vitamoments.app.data.models.enums.FriendshipDirection
import eu.vitamoments.app.data.models.enums.FriendshipStatus
import eu.vitamoments.app.data.tables.FriendshipEventTable
import eu.vitamoments.app.data.tables.FriendshipsTable
import eu.vitamoments.app.data.tables.UsersTable
import eu.vitamoments.app.dbHelpers.canonicalPair
import eu.vitamoments.app.dbHelpers.dbQuery
import eu.vitamoments.app.dbHelpers.queries.findFriendshipByPair
import eu.vitamoments.app.dbHelpers.queries.hasFriendshipExpr
import eu.vitamoments.app.dbHelpers.queries.incomingRequestsFirst
import eu.vitamoments.app.dbHelpers.queries.joinFriendships
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
import kotlin.uuid.toKotlinUuid

class JVMFriendRepository : FriendRepository {

    override suspend fun searchNewFriends(
        userId: Uuid,
        query: String?,
        limit: Int,
        offset: Int
    ): RepositoryResponse<PagedResult<PublicUser>> = dbQuery {
        val me = userId.toJavaUuid()
        val needle = query?.trim()?.takeIf { it.isNotBlank() }
        val safeLimit = limit.coerceIn(1, 50)
        val safeOffset = offset.coerceAtLeast(0).toLong()

        val hasFriendship = hasFriendshipExpr(me, UsersTable.id, FriendshipStatus.activeEntries)
        val whereExpr = UsersTable.searchPredicate(me, needle) and (hasFriendship neq Op.TRUE)

        val total: Long = UsersTable
            .select(UsersTable.id)
            .where { whereExpr }
            .count()

        if (total == 0L) {
            return@dbQuery RepositoryResponse.Success(
                PagedResult(
                    items = emptyList(),
                    limit = safeLimit,
                    offset = safeOffset,
                    total = 0,
                    hasMore = false,
                    nextOffset = null
                )
            )
        }

        val items = UsersTable
            .selectAll()
            .where { whereExpr }
            .orderBy(UsersTable.username to SortOrder.ASC)
            .limit(safeLimit)
            .offset(safeOffset)
            .toList()
            .map { it.rowToPublicResult() }

        val hasMore = safeOffset + safeLimit < total
        val nextOffset = if (hasMore) safeOffset + safeLimit else null

        RepositoryResponse.Success(
            PagedResult(
                items = items,
                limit = safeLimit,
                offset = safeOffset,
                total = total,
                hasMore = hasMore,
                nextOffset = nextOffset
            )
        )
    }

    override suspend fun searchFriends(
        userId: Uuid,
        query: String?,
        limit: Int,
        offset: Int
    ): RepositoryResponse<PagedResult<UserWithContext>> = dbQuery {
        val me = userId.toJavaUuid()
        val needle = query?.trim()?.takeIf { it.isNotBlank() }
        val safeLimit = limit.coerceIn(1, 50)
        val safeOffset = offset.coerceAtLeast(0).toLong()

        val joinFriendship = joinFriendships(me, listOf(FriendshipStatus.ACCEPTED))
        val whereExpr = UsersTable.searchPredicate(me, needle)

        val total: Long = joinFriendship
            .selectAll()
            .where { whereExpr }
            .count()

        val items = joinFriendship
            .selectAll()
            .where { whereExpr }
            .orderBy(UsersTable.username to SortOrder.ASC)
            .limit(safeLimit)
            .offset(safeOffset)
            .toList()
            .map { row ->
                UserWithContext(
                    user = row.rowToPrivateResult(),
                    friendship = AcceptedFriendship(
                        uuid = row[FriendshipsTable.id].value.toKotlinUuid(),
                        otherUserId = row[UsersTable.id].value.toKotlinUuid(),
                        createdAt = row[FriendshipsTable.createdAt].toInstant(),
                        updatedAt = row[FriendshipsTable.updatedAt].toInstant()
                    )
                )
            }

        val hasMore = safeOffset + safeLimit < total
        val nextOffset = if (hasMore) safeOffset + safeLimit else null

        RepositoryResponse.Success(
            PagedResult(
                items = items,
                limit = safeLimit,
                offset = safeOffset,
                total = total,
                hasMore = hasMore,
                nextOffset = nextOffset
            )
        )
    }

    override suspend fun friendRequests(
        userId: Uuid,
        query: String?,
        limit: Int,
        offset: Int
    ): RepositoryResponse<PagedResult<UserWithContext>> = dbQuery {
        val me = userId.toJavaUuid()
        val needle = query?.trim()?.takeIf { it.isNotBlank() }
        val safeLimit = limit.coerceIn(1, 50)
        val safeOffset = offset.coerceAtLeast(0).toLong()

        val whereExpr = UsersTable.searchPredicate(me, needle)
        val joinFriendship = joinFriendships(me, listOf(FriendshipStatus.PENDING))

        val total: Long = joinFriendship
            .selectAll()
            .where { whereExpr }
            .count()

        val items = joinFriendship
            .selectAll()
            .where { whereExpr }
            .orderBy(
                incomingRequestsFirst(me) to SortOrder.ASC,
                UsersTable.username to SortOrder.ASC
            )
            .limit(safeLimit)
            .offset(safeOffset)
            .toList()
            .map { row ->
                val direction: FriendshipDirection =
                    if (row[FriendshipsTable.fromUserId].value == me) FriendshipDirection.OUTGOING
                    else FriendshipDirection.INCOMING

                val friendship: Friendship = when (row[FriendshipsTable.status]) {
                    FriendshipStatus.PENDING -> PendingFriendship(
                        uuid = row[FriendshipsTable.id].value.toKotlinUuid(),
                        direction = direction,
                        otherUserId = row[UsersTable.id].value.toKotlinUuid(),
                        createdAt = row[FriendshipsTable.createdAt].toInstant(),
                        updatedAt = row[FriendshipsTable.updatedAt].toInstant()
                    )

                    FriendshipStatus.ACCEPTED -> AcceptedFriendship(
                        uuid = row[FriendshipsTable.id].value.toKotlinUuid(),
                        otherUserId = row[UsersTable.id].value.toKotlinUuid(),
                        createdAt = row[FriendshipsTable.createdAt].toInstant(),
                        updatedAt = row[FriendshipsTable.updatedAt].toInstant()
                    )

                    else -> error("joinFriendships(activeEntries) zou alleen PENDING/ACCEPTED moeten bevatten")
                }

                val user = when (friendship) {
                    is AcceptedFriendship -> row.rowToPrivateResult()
                    else -> row.rowToPublicResult()
                }

                UserWithContext(
                    user = user,
                    friendship = friendship
                )
            }

        val hasMore = safeOffset + safeLimit < total
        val nextOffset = if (hasMore) safeOffset + safeLimit else null

        RepositoryResponse.Success(
            PagedResult(
                items = items,
                limit = safeLimit,
                offset = safeOffset,
                total = total,
                hasMore = hasMore,
                nextOffset = nextOffset
            )
        )
    }

    override suspend fun incomingRequests(userId: Uuid): RepositoryResponse<List<PublicUser>> = dbQuery {
        val me = userId.toJavaUuid()
        val meRef = EntityID(me, UsersTable)

        val fromIdsSubQuery = FriendshipsTable
            .select(FriendshipsTable.fromUserId)
            .where { (FriendshipsTable.toUserId eq meRef) and (FriendshipsTable.status eq FriendshipStatus.PENDING) }

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

        val toIdsSubQuery = FriendshipsTable
            .select(FriendshipsTable.toUserId)
            .where { (FriendshipsTable.fromUserId eq meRef) and (FriendshipsTable.status eq FriendshipStatus.PENDING) }

        val result = UsersTable
            .selectAll()
            .where {
                UsersTable.deletedAt.isNull() and
                        (UsersTable.id inSubQuery toIdsSubQuery)
            }
            .orderBy(UsersTable.username to SortOrder.ASC)
            .toList()
            .map { it.rowToPublicResult() }

        RepositoryResponse.Success(result)
    }

    override suspend fun invite(
        userId: Uuid,
        otherId: Uuid
    ): RepositoryResponse<Friendship> = dbQuery {
        val errors = mutableListOf<RepositoryResponse.Error.FieldError>()

        if (userId == otherId) {
            errors += RepositoryResponse.Error.FieldError(
                field = "otherId",
                message = "You cannot invite yourself"
            )
        }

        val me = userId.toJavaUuid()
        val other = otherId.toJavaUuid()
        val now = LocalDateTime.nowUtc()

        val meUser = UserEntity.findById(me)
        val otherUser = UserEntity.findById(other)

        if (meUser == null) {
            errors += RepositoryResponse.Error.FieldError(
                field = "userId",
                message = "User with id: $me is not found"
            )
        }

        if (otherUser == null) {
            errors += RepositoryResponse.Error.FieldError(
                field = "otherId",
                message = "User with id: $other is not found"
            )
        }

        // ✅ Input/validation errors -> 400
        if (errors.isNotEmpty()) {
            return@dbQuery RepositoryResponse.Error.Validation(errors = errors)
        }

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
                FriendshipStatus.ACCEPTED -> {
                    // ✅ State conflict -> 409
                    return@dbQuery RepositoryResponse.Error.Conflict(
                        errors = listOf(
                            RepositoryResponse.Error.FieldError(
                                field = "friendrequest",
                                message = "Users are friends already"
                            )
                        )
                    )
                }

                FriendshipStatus.PENDING -> {
                    // ✅ State conflict -> 409
                    return@dbQuery RepositoryResponse.Error.Conflict(
                        errors = listOf(
                            RepositoryResponse.Error.FieldError(
                                field = "friendrequest",
                                message = "Invite is already pending"
                            )
                        )
                    )
                }

                FriendshipStatus.DECLINED, FriendshipStatus.REMOVED -> {
                    FriendshipEntity.findByIdAndUpdate(id) { e ->
                        e.status = FriendshipStatus.PENDING
                        e.fromUserId = meUser!!.id
                        e.toUserId = otherUser!!.id
                        e.updatedAt = now
                    } ?: return@dbQuery RepositoryResponse.Error.Internal()
                }
            }
        } else {
            val pairAUser = if (pairA == me) meUser else otherUser
            val pairBUser = if (pairB == me) meUser else otherUser

            FriendshipEntity.new {
                fromUserId = meUser!!.id
                toUserId = otherUser!!.id
                this.pairA = pairAUser!!
                this.pairB = pairBUser!!
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
    ): RepositoryResponse<Friendship> = dbQuery {
        val errors = mutableListOf<RepositoryResponse.Error.FieldError>()

        if (userId == otherId) {
            errors += RepositoryResponse.Error.FieldError(
                field = "otherId",
                message = "You cannot accept yourself"
            )
        }

        if (errors.isNotEmpty()) {
            return@dbQuery RepositoryResponse.Error.Validation(errors = errors)
        }

        val me = userId.toJavaUuid()
        val other = otherId.toJavaUuid()
        val now = LocalDateTime.nowUtc()
        val (pairA, pairB) = canonicalPair(me, other)

        val record = findFriendshipByPair(pairA, pairB)
            ?: return@dbQuery RepositoryResponse.Error.NotFound("Invite not found")

        if (record.status != FriendshipStatus.PENDING) {
            return@dbQuery RepositoryResponse.Error.Conflict(
                errors = listOf(
                    RepositoryResponse.Error.FieldError(
                        field = "friendrequest",
                        message = "Invite is not pending"
                    )
                )
            )
        }

        if (record.toUserId != me) {
            // (semantisch 403, maar als je geen Forbidden hebt: Unauthorized werkt ook)
            return@dbQuery RepositoryResponse.Error.Unauthorized("Only the receiver can accept an invite")
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
    ): RepositoryResponse<Friendship> = dbQuery {
        val errors = mutableListOf<RepositoryResponse.Error.FieldError>()

        if (userId == otherId) {
            errors += RepositoryResponse.Error.FieldError(
                field = "otherId",
                message = "You cannot remove yourself"
            )
        }

        if (errors.isNotEmpty()) {
            return@dbQuery RepositoryResponse.Error.Validation(errors = errors)
        }

        val me = userId.toJavaUuid()
        val other = otherId.toJavaUuid()
        val now = LocalDateTime.nowUtc()
        val (pairA, pairB) = canonicalPair(me, other)

        val record = findFriendshipByPair(pairA, pairB)
            ?: return@dbQuery RepositoryResponse.Error.NotFound("Friendship not found")

        val (newStatus, preferredEventTypeName, action, metaJson) = when (record.status) {
            FriendshipStatus.ACCEPTED ->
                Quad(
                    FriendshipStatus.REMOVED,
                    "REMOVED",
                    "removed",
                    """{"reason":"unfriended"}"""
                )

            FriendshipStatus.PENDING -> {
                when (me) {
                    record.fromUserId ->
                        Quad(
                            FriendshipStatus.REMOVED,
                            "CANCELLED",
                            "cancelled",
                            """{"reason":"cancelled_by_sender"}"""
                        )

                    record.toUserId ->
                        // keep DECLINED so your cooldown logic stays consistent
                        Quad(
                            FriendshipStatus.DECLINED,
                            "DECLINED",
                            "declined",
                            """{"reason":"removed_pending_by_receiver"}"""
                        )

                    else ->
                        return@dbQuery RepositoryResponse.Error.Internal()
                }
            }

            FriendshipStatus.REMOVED ->
                return@dbQuery RepositoryResponse.Error.Conflict(
                    errors = listOf(
                        RepositoryResponse.Error.FieldError(
                            field = "friendship",
                            message = "Friendship already removed"
                        )
                    )
                )

            FriendshipStatus.DECLINED ->
                return@dbQuery RepositoryResponse.Error.Conflict(
                    errors = listOf(
                        RepositoryResponse.Error.FieldError(
                            field = "friendrequest",
                            message = "Invite already declined"
                        )
                    )
                )

            else ->
                return@dbQuery RepositoryResponse.Error.Conflict(
                    errors = listOf(
                        RepositoryResponse.Error.FieldError(
                            field = "friendship",
                            message = "Unsupported status: ${record.status}"
                        )
                    )
                )
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
    ): RepositoryResponse<Friendship> = dbQuery {
        val errors = mutableListOf<RepositoryResponse.Error.FieldError>()

        if (userId == otherId) {
            errors += RepositoryResponse.Error.FieldError(
                field = "otherId",
                message = "You cannot decline yourself"
            )
        }

        if (errors.isNotEmpty()) {
            return@dbQuery RepositoryResponse.Error.Validation(errors = errors)
        }

        val me = userId.toJavaUuid()
        val other = otherId.toJavaUuid()
        val now = LocalDateTime.nowUtc()
        val (pairA, pairB) = canonicalPair(me, other)

        val record = findFriendshipByPair(pairA, pairB)
            ?: return@dbQuery RepositoryResponse.Error.NotFound("Invite not found")

        if (record.status != FriendshipStatus.PENDING) {
            return@dbQuery RepositoryResponse.Error.Conflict(
                errors = listOf(
                    RepositoryResponse.Error.FieldError(
                        field = "friendrequest",
                        message = "Invite is not pending"
                    )
                )
            )
        }

        if (record.toUserId != me) {
            return@dbQuery RepositoryResponse.Error.Unauthorized("Only the receiver can decline an invite")
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