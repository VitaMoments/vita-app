package eu.vitamoments.app.tsgen

object TsOwnership {

    /**
     * Applies:
     * - optional Contract suffix normalization
     * - external overrides (e.g. RichTextDocument -> common)
     */
    fun applyOverrides(base: Map<String, String>): Map<String, String> {
        val out = linkedMapOf<String, String>()

        // 1) base ownership
        for ((sym, owner) in base) {
            val normalized = normalizeSymbol(sym)
            out.putIfAbsent(normalized, owner)
        }

        // 2) external overrides
        for ((sym, owner) in TsConfig.externalSymbolOwners) {
            out[normalizeSymbol(sym)] = owner
        }

        return out
    }

    fun normalizeSymbol(name: String): String {
        if (!TsConfig.stripContractSuffix) return name
        return if (name.endsWith("Contract")) name.removeSuffix("Contract") else name
    }
}
