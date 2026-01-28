// TsVariantAliasGen.kt
package eu.vitamoments.app.tsgen

object TsVariantAliasGen {

    /**
     * Adds convenience aliases for sealed variants that END WITH "ITEM":
     * - export type BlogItem = FeedItem.BLOGITEM;
     * - export type TimelineItem = FeedItem.TIMELINEITEM;
     *
     * This makes Kotlin type names usable as TS symbols later (e.g. PagedResult<BlogItem>).
     */
    fun addItemVariantAliases(ts: String): String {
        val unions = collectUnions(ts)
        if (unions.isEmpty()) return ts

        val existingExports = Regex("""(?m)^export\s+(?:type|interface|enum|namespace)\s+([A-Za-z0-9_]+)\b""")
            .findAll(ts)
            .map { it.groupValues[1] }
            .toSet()

        val aliasLines = buildList {
            for ((unionName, variants) in unions) {
                for (v in variants) {
                    if (!v.endsWith("ITEM")) continue

                    val alias = TsCase.variantToPascal(v) // BLOGITEM -> BlogItem, TIMELINEITEM -> TimelineItem
                    if (alias in existingExports) continue
                    add("""export type $alias = $unionName.$v;""")
                }
            }
        }

        if (aliasLines.isEmpty()) return ts

        // Place aliases at end (before trailing newline tidy)
        return ts.trimEnd() + "\n\n" + aliasLines.joinToString("\n") + "\n"
    }

    private fun collectUnions(allTs: String): List<Pair<String, List<String>>> {
        val out = mutableListOf<Pair<String, List<String>>>()

        val typeStartRe = Regex("""(?m)^export type\s+([A-Za-z0-9_]+)\s*=\s*$""")
        val matches = typeStartRe.findAll(allTs).toList()

        for (m in matches) {
            val unionName = m.groupValues[1]
            val startIndex = m.range.last + 1
            val semi = allTs.indexOf(';', startIndex)
            if (semi == -1) continue

            val aliasBlock = allTs.substring(m.range.first, semi + 1)

            val variantRe = Regex("""\|\s*${Regex.escape(unionName)}\.([A-Za-z0-9_]+)""")
            val variants = variantRe.findAll(aliasBlock).map { it.groupValues[1] }.distinct().toList()
            if (variants.isEmpty()) continue

            // only if namespace exists (sealed-style)
            val hasNamespace = Regex("""(?m)^export namespace\s+${Regex.escape(unionName)}\b""").containsMatchIn(allTs)
            if (!hasNamespace) continue

            out += unionName to variants
        }

        return out
    }
}
