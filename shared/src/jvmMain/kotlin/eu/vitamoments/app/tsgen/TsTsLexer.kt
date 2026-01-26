package eu.vitamoments.app.tsgen

/**
 * Minimal TS lexer to find matching braces while skipping strings and comments.
 */
object TsTsLexer {

    fun findMatchingBrace(text: String, openBraceIndex: Int): Int? {
        if (openBraceIndex !in text.indices || text[openBraceIndex] != '{') return null

        var i = openBraceIndex
        var depth = 0

        var inSingle = false
        var inDouble = false
        var inTemplate = false
        var inLineComment = false
        var inBlockComment = false
        var escaped = false

        while (i < text.length) {
            val c = text[i]
            val next = if (i + 1 < text.length) text[i + 1] else '\u0000'

            if (inLineComment) {
                if (c == '\n') inLineComment = false
                i++
                continue
            }

            if (inBlockComment) {
                if (c == '*' && next == '/') {
                    inBlockComment = false
                    i += 2
                    continue
                }
                i++
                continue
            }

            if (inSingle) {
                if (!escaped && c == '\'') inSingle = false
                escaped = (!escaped && c == '\\')
                i++
                continue
            }

            if (inDouble) {
                if (!escaped && c == '"') inDouble = false
                escaped = (!escaped && c == '\\')
                i++
                continue
            }

            if (inTemplate) {
                if (!escaped && c == '`') inTemplate = false
                escaped = (!escaped && c == '\\')
                i++
                continue
            }

            if (c == '/' && next == '/') {
                inLineComment = true
                i += 2
                continue
            }
            if (c == '/' && next == '*') {
                inBlockComment = true
                i += 2
                continue
            }
            if (c == '\'') {
                inSingle = true
                escaped = false
                i++
                continue
            }
            if (c == '"') {
                inDouble = true
                escaped = false
                i++
                continue
            }
            if (c == '`') {
                inTemplate = true
                escaped = false
                i++
                continue
            }

            if (c == '{') depth++
            if (c == '}') {
                depth--
                if (depth == 0) return i
            }

            i++
        }

        return null
    }
}
