package eu.vitamoments.app.tsgen

/**
 * kxs-ts-gen emits BOTH:
 *  - top-level `export interface BLOGITEM { ... }` (often without discriminator)
 *  - namespaced `export namespace FeedItem { export interface BLOGITEM { type: ... } }`
 *
 * For API DTOs you typically only want the namespaced discriminated variants.
 *
 * This cleanup removes ONLY the top-level interfaces that duplicate the namespaced variants,
 * leaving the namespace variants intact.
 */
object TsSealedCleanup {

    fun removeTopLevelDuplicateVariantInterfaces(ts: String): String {
        var out = ts

        // Find sealed union aliases like:
        // export type FeedItem =
        //   | FeedItem.BLOGITEM
        //   | FeedItem.TIMELINEITEM;
        val unionNames = Regex("""(?m)^export type\s+([A-Za-z0-9_]+)\s*=""")
            .findAll(out)
            .map { it.groupValues[1] }
            .toList()

        for (union in unionNames) {
            // Collect variant symbols referenced as Union.VARIANT
            val variants = Regex("""\b${Regex.escape(union)}\.([A-Za-z0-9_]+)\b""")
                .findAll(out)
                .map { it.groupValues[1] }
                .toSet()

            if (variants.isEmpty()) continue

            // Remove ONLY top-level (column 0) interfaces for those variant names
            for (variant in variants) {
                out = removeTopLevelBraceBlock(out, "export interface", variant)
            }
        }

        // Tidy any excessive whitespace caused by removals
        out = out.replace(Regex("\n{3,}"), "\n\n")
        return out
    }

    /**
     * Removes a block like:
     * export interface NAME { ... }
     *
     * Only if it starts at column 0 (top-level). Indented versions (inside namespaces)
     * are NOT removed.
     */
    private fun removeTopLevelBraceBlock(input: String, prefix: String, symbol: String): String {
        var out = input
        val re = Regex("""(?m)^${Regex.escape(prefix)}\s+${Regex.escape(symbol)}\b""")

        while (true) {
            val m = re.find(out) ?: return out
            val start = m.range.first

            // Find first '{' after the match start
            val open = out.indexOf('{', startIndex = start)
            if (open == -1) return out

            val close = TsTsLexer.findMatchingBrace(out, open) ?: return out

            // Remove block + trailing whitespace/newlines
            val end = consumeTrailingWhitespace(out, close + 1)
            out = out.removeRange(start, end)
        }
    }

    private fun consumeTrailingWhitespace(s: String, from: Int): Int {
        var i = from
        while (i < s.length) {
            val c = s[i]
            if (c == '\n' || c == '\r' || c == ' ' || c == '\t') i++ else break
        }
        return i
    }
}
