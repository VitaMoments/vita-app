package eu.vitamoments.app.data.mapper.extension_functions

import kotlin.math.abs

/**
 * Fixed-point scale: 6 decimals
 * valueScaled = realValue * NEVO_SCALE
 */
const val NEVO_SCALE: Long = 1_000_000L

/**
 * Parse a numeric string (CSV cell) to scaled Long.
 * Accepts comma or dot as decimal separator.
 * Returns null on null/blank/invalid.
 */
fun String?.toNevoScaledOrNull(): Long? {
    val s0 = this?.trim() ?: return null
    if (s0.isEmpty()) return null

    val s = s0.replace(',', '.')
    val neg = s.startsWith('-')
    val t = if (neg) s.drop(1) else s

    val parts = t.split('.', limit = 2)
    val wholePart = parts[0].ifEmpty { "0" }
    if (wholePart.any { !it.isDigit() }) return null

    val fracPartRaw = parts.getOrNull(1) ?: ""
    if (fracPartRaw.any { !it.isDigit() }) return null

    val whole = wholePart.toLong()
    val frac = fracPartRaw
        .padEnd(6, '0')
        .take(6)
        .toLongOrNull() ?: 0L

    val raw = whole * NEVO_SCALE + frac
    return if (neg) -raw else raw
}

/**
 * Convert scaled Long to string.
 * Examples:
 * 3400000 -> "3.4"
 * 120000000 -> "120"
 * 1234567 -> "1.234567"
 */
fun Long.toNevoString(): String {
    val neg = this < 0
    val a = abs(this)

    val whole = a / NEVO_SCALE
    val fracRaw = (a % NEVO_SCALE).toString().padStart(6, '0')
    val fracTrimmed = fracRaw.trimEnd('0')

    val base = if (fracTrimmed.isEmpty()) whole.toString() else "$whole.$fracTrimmed"
    return if (neg) "-$base" else base
}

/**
 * Round scaled value to [decimals] decimal places (0..6) using HALF_UP.
 * Returns a scaled Long.
 */
fun Long.roundNevo(decimals: Int): Long {
    require(decimals in 0..6) { "decimals must be 0..6" }

    // step = 10^(6-decimals): how many raw units represent 1 "kept" step
    val step = when (decimals) {
        0 -> 1_000_000L
        1 -> 100_000L
        2 -> 10_000L
        3 -> 1_000L
        4 -> 100L
        5 -> 10L
        else -> 1L
    }

    val sign = if (this < 0) -1 else 1
    val a = abs(this)
    val half = step / 2
    val rounded = ((a + half) / step) * step
    return rounded * sign
}

/**
 * Convert per-100g scaled value to per-portion (grams) scaled value, HALF_UP.
 * Example: per100g * 30 / 100.
 */
fun Long.perPortionFromPer100g(portionGrams: Int): Long {
    val numerator = this * portionGrams
    return if (numerator >= 0) (numerator + 50) / 100 else (numerator - 50) / 100
}
