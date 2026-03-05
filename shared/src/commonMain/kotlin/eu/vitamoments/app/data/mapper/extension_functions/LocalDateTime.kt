package eu.vitamoments.app.data.mapper.extension_functions

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

fun LocalDateTime.toLong() : Long = this.toInstant(TimeZone.UTC).toEpochMilliseconds()

fun LocalDateTime.toInstant() : Instant = this.toInstant(TimeZone.UTC)

/**
 * Converts this LocalDate to an Instant at the start of the day (00:00) in the given timezone.
 */
fun LocalDate.toInstantStartOfDay(timeZone: TimeZone = TimeZone.UTC): Instant =
    this.atStartOfDayIn(timeZone)

/**
 * Converts this LocalDate to an Instant at the end of the day (23:59:59.999999999) in the given timezone.
 * Useful for inclusive date range filters.
 */
fun LocalDate.toInstantEndOfDay(timeZone: TimeZone = TimeZone.UTC): Instant {
    val endOfDay = LocalDateTime(this.year, this.monthNumber, this.dayOfMonth, 23, 59, 59, 999_999_999)
    return endOfDay.toInstant(timeZone)
}

fun LocalDateTime.nowUtc() : LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)

fun LocalDateTime.Companion.nowUtc() : LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)

fun LocalDateTime.minusDays(days: Int): LocalDateTime =
    this.toInstant(TimeZone.UTC)
        .minus(days.toLong(), DateTimeUnit.DAY, TimeZone.UTC)
        .toLocalDateTime(TimeZone.UTC)

fun LocalDateTime.minus(duration: Duration): LocalDateTime =
    this.toInstant(TimeZone.UTC)
        .minus(duration)
        .toLocalDateTime(TimeZone.UTC)


fun LocalDateTime.Companion.nowPlusSeconds(
    seconds: Long,
    zone: TimeZone = TimeZone.UTC
): LocalDateTime =
    (Clock.System.now() + seconds.seconds)
        .toLocalDateTime(zone)

fun LocalDateTime.Companion.from(
    epochSeconds: Long,
    nanosecondAdjustment: Long = 0,
    timeZone: TimeZone = TimeZone.UTC
) : LocalDateTime = Instant.fromEpochSeconds(
    epochSeconds = epochSeconds,
    nanosecondAdjustment = nanosecondAdjustment
).toLocalDateTime(timeZone)

fun LocalDateTime.secondsFromNow(
    zone: TimeZone = TimeZone.UTC
): Long =
    (this.toInstant(zone) - Clock.System.now())
        .inWholeSeconds
        .coerceAtLeast(0)