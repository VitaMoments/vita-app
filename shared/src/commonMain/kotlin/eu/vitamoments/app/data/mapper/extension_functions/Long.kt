package eu.vitamoments.app.data.mapper.extension_functions

private const val SCALE: Long = 1_000_000L

fun parseScaledDecimal(input: String?): Long? {
    val s = input?.trim()?.replace(',', '.') ?: return null
    if (s.isEmpty()) return null

    val neg = s.startsWith('-')
    val t = if (neg) s.substring(1) else s
    val parts = t.split('.', limit = 2)

    val whole = parts[0].ifEmpty { "0" }.toLong()
    val fracStr = (parts.getOrNull(1) ?: "")
        .padEnd(6, '0')
        .take(6)

    val frac = fracStr.ifEmpty { "0" }.toLong()
    val raw = whole * SCALE + frac
    return if (neg) -raw else raw
}

fun Long.toDecimalString(): String {
    val neg = this < 0
    val abs = kotlin.math.abs(this)
    val whole = abs / SCALE
    val frac = (abs % SCALE).toString().padStart(6, '0').trimEnd('0')
    val base = if (frac.isEmpty()) whole.toString() else "$whole.$frac"
    return if (neg) "-$base" else base
}
