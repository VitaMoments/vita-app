// TsPostProcess.kt
package eu.vitamoments.app.tsgen

object TsPostProcess {

    fun process(
        rawTs: String,
        headerDate: String,
        ownership: Map<String, String>,
        currentModule: String
    ): String {
        var out = rawTs

        // 1) Normalize header
        out = normalizeHeader(out, headerDate)

        // 2) Strip Contract suffix everywhere
        if (TsConfig.stripContractSuffix) {
            out = TsRename.stripContractSuffixEverywhere(out)
        }

        // 3) REMOVE exported helper symbols FIRST (before doing replacements)
        (TsConfig.dropSymbols + TsConfig.manualOverrideSymbols).forEach { sym ->
            out = TsRemove.removeExportedSymbol(out, sym)
        }

        // 4) Replace remaining references
        out = out.replace(Regex("""\bJsonElement\b"""), "any")
        out = out.replace(Regex("""\bJsonNull\b"""), "null")

        // Safety cleanup in case something slipped through
        out = out.replace(Regex("""(?m)^export type any = any;\s*$\R?"""), "")

        // 5) Strip symbols that belong to other modules (including external overrides)
        val foreignSymbols = ownership
            .filterValues { it != currentModule }
            .keys

        foreignSymbols.forEach { sym ->
            out = TsRemove.removeExportedSymbol(out, sym)
        }

        // ✅ 6) Remove top-level duplicate interfaces for sealed union variants
        out = TsSealedCleanup.removeTopLevelDuplicateVariantInterfaces(out)

        // 7) Auto-import still referenced foreign symbols
        val usedImports = linkedMapOf<String, MutableSet<String>>() // from -> symbols

        foreignSymbols.forEach { sym ->
            val owner = ownership[sym] ?: return@forEach
            if (owner == currentModule) return@forEach

            val re = Regex("""\b${Regex.escape(sym)}\b""")
            if (re.containsMatchIn(out)) {
                usedImports.getOrPut("./$owner") { mutableSetOf() }.add(sym)
            }
        }

        if (usedImports.isNotEmpty()) {
            val importBlock = buildString {
                usedImports.toSortedMap().forEach { (from, syms) ->
                    val names = syms.toList().sorted()
                    appendLine("""import type { ${names.joinToString(", ")} } from "$from";""")
                }
                appendLine()
            }
            out = insertAfterHeader(out, importBlock)
        }

        out = out.replace(Regex("\n{3,}"), "\n\n").trimEnd() + "\n"
        return out
    }

    private fun normalizeHeader(ts: String, headerDate: String): String {
        val headerRegex = Regex(
            pattern = """\A// GENERATED FILE - DO NOT EDIT\s*\R// Source: Kotlin @Serializable DTOs \(kxs-ts-gen\)\s*(?:\R// Date:.*)?\s*\R\R""",
            options = setOf(RegexOption.MULTILINE)
        )

        val normalizedHeader = """// GENERATED FILE - DO NOT EDIT
// Source: Kotlin @Serializable DTOs (kxs-ts-gen)
// Date: $headerDate

"""

        return if (headerRegex.containsMatchIn(ts)) {
            ts.replaceFirst(headerRegex, normalizedHeader)
        } else {
            normalizedHeader + ts
        }
    }

    private fun insertAfterHeader(ts: String, insert: String): String {
        val headerEnd = ts.indexOf("\n\n")
        return if (headerEnd != -1) {
            ts.substring(0, headerEnd + 2) + insert + ts.substring(headerEnd + 2)
        } else {
            insert + ts
        }
    }
}
