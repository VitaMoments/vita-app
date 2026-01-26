// TsTypeGuardsGen.kt
package eu.vitamoments.app.tsgen

object TsTypeGuardsGen {

    data class Union(
        val name: String,
        val variants: List<String>
    )

    /**
     * Scan ALL generated module TS files and find discriminated unions in this shape:
     *
     * export type User =
     *   | User.ACCOUNT
     *   | User.PUBLIC;
     *
     * export namespace User {
     *   export enum Type { ... }
     *   export interface ACCOUNT { type: User.Type.ACCOUNT; ... }
     * }
     *
     * We generate:
     * export const isUserAccount = (x: User): x is User.ACCOUNT => x.type === User.Type.ACCOUNT;
     */
    fun generate(
        headerDate: String,
        moduleTsByName: Map<String, String>
    ): String {
        val unions = collectUnions(moduleTsByName.values.joinToString("\n\n"))
            .sortedBy { it.name }

        val importedTypes = unions.map { it.name }.distinct().sorted()

        val body = buildString {
            appendLine("// GENERATED FILE - DO NOT EDIT")
            appendLine("// Source: Kotlin @Serializable DTOs (kxs-ts-gen)")
            appendLine("// Date: $headerDate")
            appendLine()
            appendLine("""import { ${importedTypes.joinToString(", ")} } from "./index";""")
            appendLine()

            unions.forEach { u ->
                appendLine("/**")
                appendLine(" * -------------------------")
                appendLine(" * ${u.name} type guards")
                appendLine(" * -------------------------")
                appendLine(" */")

                u.variants.forEach { v ->
                    val fn = "is${u.name}${TsCase.variantToPascal(v)}"
                    appendLine(
                        """export const $fn = (x: ${u.name}): x is ${u.name}.$v => x.type === ${u.name}.Type.$v;"""
                    )
                }
                appendLine()
            }
        }

        return body.trimEnd() + "\n"
    }

    private fun collectUnions(allTs: String): List<Union> {
        val unions = mutableListOf<Union>()

        // Find "export type X =" blocks
        val typeStartRe = Regex("""(?m)^export type\s+([A-Za-z0-9_]+)\s*=\s*$""")
        val matches = typeStartRe.findAll(allTs).toList()

        for (m in matches) {
            val unionName = m.groupValues[1]
            val startIndex = m.range.last + 1

            // Read forward until semicolon line of this type alias (first ';' after start)
            val semi = allTs.indexOf(';', startIndex)
            if (semi == -1) continue

            val aliasBlock = allTs.substring(m.range.first, semi + 1)

            // Collect lines containing: | Union.VARIANT
            val variantRe = Regex("""\|\s*${Regex.escape(unionName)}\.([A-Za-z0-9_]+)""")
            val variants = variantRe.findAll(aliasBlock).map { it.groupValues[1] }.toList()
            if (variants.isEmpty()) continue

            // Only generate guards if it looks like a discriminated union namespace exists with Type enum
            // This avoids generating guards for random union aliases that aren't sealed-discriminator-based.
            val hasNamespace = Regex("""(?m)^export namespace\s+${Regex.escape(unionName)}\b""").containsMatchIn(allTs)
            val hasTypeEnum = Regex("""(?m)^\s*export enum\s+Type\b""").containsMatchIn(
                extractNamespaceBlock(allTs, unionName) ?: ""
            )
            if (!hasNamespace || !hasTypeEnum) continue

            unions += Union(unionName, variants.distinct())
        }

        return unions
    }

    private fun extractNamespaceBlock(allTs: String, unionName: String): String? {
        val needle = "export namespace $unionName"
        val start = allTs.indexOf(needle)
        if (start == -1) return null

        val open = allTs.indexOf('{', startIndex = start)
        if (open == -1) return null

        val close = TsTsLexer.findMatchingBrace(allTs, open) ?: return null
        return allTs.substring(start, close + 1)
    }
}
