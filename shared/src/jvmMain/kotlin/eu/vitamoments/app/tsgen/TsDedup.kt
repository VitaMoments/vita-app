// TsDedup.kt
package eu.vitamoments.app.tsgen

/**
 * Removes duplicate top-level `export namespace X { ... }` blocks (column 0).
 *
 * This fixes cases where the generator emits the same namespace multiple times
 * (e.g. dependencies repeated across multiple serializers in one generate() run).
 *
 * Keeps the FIRST namespace block per name, removes later duplicates.
 */
object TsDedup {

    fun removeDuplicateTopLevelNamespaces(ts: String): String {
        var out = ts
        val seen = mutableSetOf<String>()

        val re = Regex("""(?m)^export namespace\s+([A-Za-z0-9_]+)\b""")

        var cursor = 0
        while (true) {
            val m = re.find(out, cursor) ?: break
            val name = m.groupValues[1]
            val start = m.range.first

            val open = out.indexOf('{', startIndex = start)
            if (open == -1) {
                cursor = m.range.last + 1
                continue
            }

            val close = TsTsLexer.findMatchingBrace(out, open) ?: run {
                cursor = m.range.last + 1
                continue
            }

            val end = consumeTrailingWhitespace(out, close + 1)

            if (seen.add(name)) {
                // keep first
                cursor = end
            } else {
                // remove duplicate block
                out = out.removeRange(start, end)
                // cursor stays at start because string got shorter
                cursor = start
            }
        }

        out = out.replace(Regex("\n{3,}"), "\n\n")
        return out.trimEnd() + "\n"
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