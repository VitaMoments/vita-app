package eu.vitamoments.app.tsgen

/**
 * Post-process generated TS:
 * - Ensures header contains a single Date line
 * - Injects imports
 * - Strips exported symbols (so we can import them from other files)
 *
 * Pragmatic regex-based approach tailored to kxs-ts-gen output.
 */
object TsPostProcess {

    data class Import(val symbol: String, val from: String)

    fun process(
        rawTs: String,
        headerDate: String,
        imports: List<Import>,
        stripExports: List<String>,
    ): String {
        var out = rawTs

        // --- 1) Normalize header to ALWAYS have exactly one Date line ---
        // Supports both:
        // - header without Date
        // - header with Date already present
        //
        // We match the top header block and rebuild it.
        val headerRegex = Regex(
            pattern = """\A// GENERATED FILE - DO NOT EDIT\s*\R// Source: Kotlin @Serializable DTOs \(kxs-ts-gen\)\s*(?:\R// Date:.*)?\s*\R\R""",
            options = setOf(RegexOption.MULTILINE)
        )

        out = if (headerRegex.containsMatchIn(out)) {
            out.replaceFirst(
                headerRegex,
                """// GENERATED FILE - DO NOT EDIT
// Source: Kotlin @Serializable DTOs (kxs-ts-gen)
// Date: $headerDate

"""
            )
        } else {
            // If generator ever changes header format, we still ensure our header exists at top
            """// GENERATED FILE - DO NOT EDIT
// Source: Kotlin @Serializable DTOs (kxs-ts-gen)
// Date: $headerDate

$out"""
        }

        // --- 2) Strip exported symbols we want to import instead ---
        stripExports.forEach { symbol ->
            out = removeSymbol(out, symbol)
        }

        // --- 3) Inject imports right after header block ---
        if (imports.isNotEmpty()) {
            val importLines = buildString {
                imports
                    .groupBy { it.from }
                    .forEach { (from, items) ->
                        val names = items.map { it.symbol }.distinct().sorted()
                        appendLine("""import type { ${names.joinToString(", ")} } from "$from";""")
                    }
                appendLine()
            }

            // Header ends with a blank line; insert imports after that.
            val headerEnd = out.indexOf("\n\n")
            out = if (headerEnd != -1) {
                out.substring(0, headerEnd + 2) + importLines + out.substring(headerEnd + 2)
            } else {
                importLines + out
            }
        }

        // --- 4) Tidy ---
        out = out.replace(Regex("\n{3,}"), "\n\n").trimEnd() + "\n"
        return out
    }

    private fun removeSymbol(input: String, symbol: String): String {
        var out = input

        // export type X = ...;   (including multi-line unions)
        out = out.replace(
            Regex("""export type\s+$symbol\s*=\s*[\s\S]*?;\s*\n"""),
            ""
        )

        // export namespace X { ... }
        out = out.replace(
            Regex("""export namespace\s+$symbol\s*\{[\s\S]*?}\s*\n"""),
            ""
        )

        // export interface X { ... }
        out = out.replace(
            Regex("""export interface\s+$symbol\s*\{[\s\S]*?}\s*\n"""),
            ""
        )

        // export enum X { ... }
        out = out.replace(
            Regex("""export enum\s+$symbol\s*\{[\s\S]*?}\s*\n"""),
            ""
        )

        return out
    }
}
