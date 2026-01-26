package eu.vitamoments.app.tsgen

/**
 * Removes exported symbols safely (brace-aware) so nested namespaces don't leave garbage behind.
 */
object TsRemove {

    fun removeExportedSymbol(input: String, symbol: String): String {
        var out = input

        out = removeExportTypeAlias(out, symbol)
        out = removeBraceBlock(out, "export interface", symbol)
        out = removeBraceBlock(out, "export enum", symbol)
        out = removeBraceBlock(out, "export namespace", symbol)

        return out
    }

    private fun removeExportTypeAlias(input: String, symbol: String): String {
        var out = input
        while (true) {
            val idx = out.indexOf("export type $symbol")
            if (idx == -1) return out

            val semi = out.indexOf(';', startIndex = idx)
            if (semi == -1) return out

            val end = consumeTrailingWhitespace(out, semi + 1)
            out = out.removeRange(idx, end)
        }
    }

    private fun removeBraceBlock(input: String, prefix: String, symbol: String): String {
        var out = input
        while (true) {
            val needle = "$prefix $symbol"
            val start = out.indexOf(needle)
            if (start == -1) return out

            val open = out.indexOf('{', startIndex = start)
            if (open == -1) return out

            val close = TsTsLexer.findMatchingBrace(out, open) ?: return out

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
