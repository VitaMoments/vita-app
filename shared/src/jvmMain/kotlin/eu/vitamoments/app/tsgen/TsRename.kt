package eu.vitamoments.app.tsgen

/**
 * Rename pass: strip "Contract" suffix everywhere.
 * This enforces: Kotlin FooContract -> TS Foo.
 */
object TsRename {

    fun stripContractSuffixEverywhere(ts: String): String {
        return Regex("""\b([A-Za-z0-9_]+)Contract\b""")
            .replace(ts) { mr -> mr.groupValues[1] }
    }
}
