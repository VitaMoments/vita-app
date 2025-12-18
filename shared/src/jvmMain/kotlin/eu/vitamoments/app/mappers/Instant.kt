@file:OptIn(ExperimentalTime::class)

package eu.vitamoments.app.mappers

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

fun Instant.toUtcLocalDateTime(): LocalDateTime = this.toLocalDateTime(TimeZone.UTC)