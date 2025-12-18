package eu.vitamoments.app.mappers

import java.time.Instant
import java.util.Date

fun Date.epochSeconds() : Long = toInstant().epochSecond
fun Date.fromEpochSeconds(epochSeconds: Long) = Date.from(Instant.ofEpochSecond(epochSeconds))