// TsConfig.kt
package eu.vitamoments.app.tsgen

object TsConfig {
    const val contractsRoot: String = "eu.vitamoments.app.api.contracts"

    val requiredModules: Set<String> = setOf("common", "enums")
    val alwaysWriteModules: Set<String> = setOf("common")

    // ✅ Kotlin FooContract -> TS Foo
    const val stripContractSuffix: Boolean = true

    // Force ownership for shared symbols
    val externalSymbolOwners: Map<String, String> = mapOf(
        "RichTextDocument" to "common"
    )

    // Drop these completely (and also prevent the "export type any = any" artifact)
    val dropSymbols: Set<String> = setOf(
        "JsonNull",
        "JsonElement"
    )

    /**
     * ✅ Symbols that MUST be manual (generator output is always removed).
     * Reason: we want 1 canonical definition in common.ts.
     */
    val manualOverrideSymbols: Set<String> = setOf(
        "RichTextDocument"
    )
}
