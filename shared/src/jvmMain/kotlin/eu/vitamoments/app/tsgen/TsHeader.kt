// TsHeader.kt
package eu.vitamoments.app.tsgen

object TsHeader {
    private const val MARKER = "// GENERATED FILE - DO NOT EDIT"

    data class Split(val header: String?, val body: String)

    /**
     * Header is defined as:
     * - starts at the first line that contains MARKER
     * - continues while lines start with "//"
     * - stops at first non-comment line (or EOF)
     *
     * Works with/without an empty line after the header.
     */
    fun split(ts: String): Split {
        val start = ts.indexOf(MARKER)
        if (start == -1) return Split(header = null, body = ts)

        val end = findHeaderEndByCommentLines(ts, start) ?: return Split(header = null, body = ts)

        val header = ts.substring(start, end)
        val body = ts.removeRange(start, end).trimStart()
        return Split(header = header, body = body)
    }

    /**
     * Ensures the header exists exactly once and is at the top.
     * If something is before the header, it gets moved after the header.
     * If multiple headers exist, keeps the first one and removes the rest.
     */
    fun ensureHeaderFirst(ts: String): String {
        val start = ts.indexOf(MARKER)
        if (start == -1) return ts

        val end = findHeaderEndByCommentLines(ts, start) ?: return ts

        val header = ts.substring(start, end)

        val prefix = ts.substring(0, start).trimStart()
        var rest = ts.substring(end).trimStart()

        // Remove any additional headers from the rest
        rest = removeAllAdditionalHeaders(rest)

        val body = buildString {
            if (prefix.isNotBlank()) {
                append(prefix.trimEnd())
                append("\n\n")
            }
            append(rest.trimStart())
        }.trimStart()

        // Always keep exactly one blank line after header for nice formatting
        val headerWithSpacing = header.trimEnd() + "\n\n"

        return headerWithSpacing + body
    }

    private fun removeAllAdditionalHeaders(input: String): String {
        var s = input
        while (true) {
            val start = s.indexOf(MARKER)
            if (start == -1) return s
            val end = findHeaderEndByCommentLines(s, start) ?: return s.removeRange(start, s.length)
            s = s.removeRange(start, end).trimStart()
        }
    }

    /**
     * Finds end index (exclusive) of the header by scanning lines.
     * A "comment line" is any line whose trimmed-left begins with "//".
     */
    private fun findHeaderEndByCommentLines(s: String, start: Int): Int? {
        var i = start
        var lineStart = start

        // Move to start of the line containing MARKER (safe if MARKER isn't at col 0)
        run {
            var j = start
            while (j > 0 && s[j - 1] != '\n' && s[j - 1] != '\r') j--
            lineStart = j
        }

        i = lineStart

        while (i < s.length) {
            val (line, nextIndex) = readLineWithEndIndex(s, i)
            val trimmedLeft = line.dropWhile { it == ' ' || it == '\t' }

            // Header continues only while lines start with "//"
            if (!trimmedLeft.startsWith("//")) {
                return i
            }

            i = nextIndex
        }

        // EOF reached: header goes to EOF
        return s.length
    }

    /**
     * Returns (lineWithoutNewline, indexAfterLineBreak).
     * Supports LF and CRLF.
     */
    private fun readLineWithEndIndex(s: String, from: Int): Pair<String, Int> {
        if (from >= s.length) return "" to s.length
        val lf = s.indexOf('\n', from)
        val cr = s.indexOf('\r', from)

        val end = when {
            lf == -1 && cr == -1 -> s.length
            lf == -1 -> cr
            cr == -1 -> lf
            else -> minOf(lf, cr)
        }

        val line = s.substring(from, end)

        var next = end
        // consume newline(s)
        if (next < s.length && s[next] == '\r') next++
        if (next < s.length && s[next] == '\n') next++
        return line to next
    }
}
