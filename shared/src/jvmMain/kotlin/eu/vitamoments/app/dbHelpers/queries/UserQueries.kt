package eu.vitamoments.app.dbHelpers.queries

import eu.vitamoments.app.data.tables.UsersTable
import org.jetbrains.exposed.v1.core.LowerCase
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.isNotNull
import org.jetbrains.exposed.v1.core.isNull
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.core.neq
import org.jetbrains.exposed.v1.core.or

fun UsersTable.searchPredicate(
    meId: java.util.UUID,
    needle: String?,
    includeSelf: Boolean = false,
    includeRemoved: Boolean = true,
): Op<Boolean> {
    val excludeMe = if (includeSelf) Op.TRUE else (UsersTable.id neq meId)
    val excludeRemoved = if (includeRemoved) Op.TRUE else (UsersTable.deletedAt.isNull())
    val base = excludeMe and excludeRemoved

    val q = needle?.trim()?.takeIf { it.isNotBlank() }?.lowercase()
    if (q == null) return base

    val pattern = "%$q%"

    val aliasExistsAndMatches =
        this.alias.isNotNull() and (LowerCase(this.alias) like pattern)
    val aliasMissingSoMatchUsername =
        this.alias.isNull() and (LowerCase(this.username) like pattern)
    val matches = aliasExistsAndMatches or aliasMissingSoMatchUsername

    return base and matches
}

