package eu.vitamoments.app.tsgen

object TsManual {
    /**
     * Generic wrapper can't be generated reliably via serializers without concrete T.
     * So we emit a TS generic once, based on the Kotlin contract shape.
     */
    fun pagedResultContractTs(): String = """
// ---- Manually emitted generic contracts ----
// Kotlin generic @Serializable wrappers cannot be generated automatically without concrete type args.

export interface PagedResultContract<T> {
  items: T[];
  limit: number;
  offset: number;
  total: number;
  hasMore: boolean;
  nextOffset?: number | null;
}

""".trimIndent() + "\n"
}
