// TsCase.kt
package eu.vitamoments.app.tsgen

object TsCase {

    /**
     * Converts enum-like variants to readable PascalCase for function names.
     *
     * Examples:
     * - PUBLIC -> Public
     * - PRIVATE -> Private
     * - BLOGITEM -> BlogItem
     * - TIMELINEITEM -> TimelineItem
     *
     * We do a small heuristic for common suffixes like ITEM/DTO/ID/URL/UUID.
     */
    fun variantToPascal(raw: String): String {
        val v = raw.trim()
        if (v.isEmpty()) return v

        // If contains underscores, treat as tokens
        if (v.contains("_")) {
            return v.split("_").filter { it.isNotBlank() }.joinToString("") { token ->
                token.lowercase().replaceFirstChar { it.uppercaseChar() }
            }
        }

        // If it's not all-caps, just Pascal it (best effort)
        val isAllCaps = v.all { !it.isLetter() || it.isUpperCase() }
        if (!isAllCaps) {
            return v.replaceFirstChar { it.uppercaseChar() }
        }

        // Heuristic split for common suffixes
        val knownSuffixes = listOf("UUID", "URL", "ID", "DTO", "ITEM")
        for (suffix in knownSuffixes) {
            if (v.length > suffix.length && v.endsWith(suffix)) {
                val prefix = v.removeSuffix(suffix)
                return prefix.lowercase().replaceFirstChar { it.uppercaseChar() } +
                        suffix.lowercase().replaceFirstChar { it.uppercaseChar() }
            }
        }

        // Fallback: just TitleCase the whole word
        return v.lowercase().replaceFirstChar { it.uppercaseChar() }
    }
}
