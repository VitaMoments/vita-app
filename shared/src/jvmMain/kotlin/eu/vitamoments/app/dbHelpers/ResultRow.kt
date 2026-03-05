package eu.vitamoments.app.dbHelpers

import eu.vitamoments.app.data.mapper.extension_functions.toInstant
import eu.vitamoments.app.data.mapper.extension_functions.toInstantEndOfDay
import eu.vitamoments.app.data.mapper.extension_functions.toInstantStartOfDay
import eu.vitamoments.app.data.tables.base.Timestamps
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ResultRow
import kotlin.time.Instant

fun ResultRow.createdAt(of: Timestamps): Instant =
    this.instant(of.createdAt)

fun ResultRow.updatedAt(of: Timestamps): Instant =
    this.instant(of.updatedAt)

fun ResultRow.deletedAt(of: Timestamps): Instant? =
    this.instantOrNull(of.deletedAt)

fun ResultRow.instant(column: Column<LocalDate>, startOfDay: Boolean = true, timeZone: TimeZone = TimeZone.UTC): Instant =
    if (startOfDay)
        this[column].toInstantStartOfDay(timeZone = timeZone)
    else
        this[column].toInstantEndOfDay(timeZone = timeZone)

fun ResultRow.instantOrNull(column: Column<LocalDate?>, startOfDay: Boolean = true, timeZone: TimeZone = TimeZone.UTC): Instant? =
    if (startOfDay)
        this[column]?.toInstantStartOfDay(timeZone = timeZone)
    else
        this[column]?.toInstantEndOfDay(timeZone = timeZone)

fun ResultRow.instant(column: Column<LocalDateTime>): Instant =
    this[column].toInstant()

fun ResultRow.instantOrNull(column: Column<LocalDateTime?>): Instant? =
    this[column]?.toInstant()