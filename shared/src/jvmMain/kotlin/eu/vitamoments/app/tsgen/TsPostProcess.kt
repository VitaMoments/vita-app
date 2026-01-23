package eu.vitamoments.app.tsgen

object TsPostProcess {

    data class Import(val symbol: String, val from: String)

    /**
     * Full automatic mode:
     * - Normalizes header
     * - Removes symbols owned by other modules
     * - Detects which external symbols are referenced
     * - Injects ONLY those imports
     */
    fun processAutoImports(
        rawTs: String,
        headerDate: String,
        ownership: Map<String, String>,   // symbol -> moduleName
        currentModule: String
    ): String {
        var out = rawTs

        // --- 1) Normalize header (always single Date line) ---
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
            """// GENERATED FILE - DO NOT EDIT
// Source: Kotlin @Serializable DTOs (kxs-ts-gen)
// Date: $headerDate

$out"""
        }

        // --- 2) Strip symbols that belong to other modules ---
        // We strip definitions, but references remain -> we will import them.
        val foreignSymbols = ownership
            .filterValues { it != currentModule }
            .keys

        foreignSymbols.forEach { sym ->
            out = removeSymbol(out, sym)
        }

        // --- 3) Detect which external symbols are still referenced ---
        val usedImports = mutableMapOf<String, MutableSet<String>>() // from -> symbols

        foreignSymbols.forEach { sym ->
            val owner = ownership[sym] ?: return@forEach
            if (owner == currentModule) return@forEach

            // Word-boundary match (avoid partial hits)
            val re = Regex("""\b${Regex.escape(sym)}\b""")
            if (re.containsMatchIn(out)) {
                usedImports.getOrPut("./$owner") { mutableSetOf() }.add(sym)
            }
        }

        // --- 4) Inject imports after header ---
        if (usedImports.isNotEmpty()) {
            val importBlock = buildString {
                usedImports.toSortedMap().forEach { (from, syms) ->
                    val names = syms.toList().sorted()
                    appendLine("""import type { ${names.joinToString(", ")} } from "$from";""")
                }
                appendLine()
            }

            val headerEnd = out.indexOf("\n\n")
            out = if (headerEnd != -1) {
                out.substring(0, headerEnd + 2) + importBlock + out.substring(headerEnd + 2)
            } else {
                importBlock + out
            }
        }

        // --- 5) Tidy ---
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
