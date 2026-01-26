// TsManual.kt
package eu.vitamoments.app.tsgen

object TsManual {

    /**
     * Adds manual TS blocks to certain modules.
     * - common.ts: RichTextDocument + PagedResult<T>
     *
     * Guarantees:
     * - Header is always the first lines in the file (TsHeader.ensureHeaderFirst)
     * - Manual blocks are inserted right after the header
     */
    fun appendManuals(moduleName: String, ts: String): String {
        var out = ts.trimEnd()

        // Only common gets these shared/manual types
        if (moduleName == "common") {
            out = injectAfterHeader(out, commonManualTs())
        }

        // Hard guarantee: header ends up on top (even if manual contains header-ish lines)
        out = TsHeader.ensureHeaderFirst(out)

        return out.trimEnd() + "\n"
    }

    private fun injectAfterHeader(ts: String, insert: String): String {
        val split = TsHeader.split(ts)
        val manualBlock = insert.trimEnd() + "\n\n"

        return if (split.header == null) {
            // No header found: put manual first; ensureHeaderFirst() will reorder if needed
            manualBlock + ts.trimStart()
        } else {
            // header + manual + rest
            split.header.trimEnd() + "\n\n" + manualBlock + split.body.trimStart()
        }
    }

    /**
     * Manual shared types for common.ts
     */
    private fun commonManualTs(): String = """
// ---- Manually emitted shared types ----
// RichTextDocument + PagedResult<T> are shared across many contracts,
// and should live in common.ts (not repeated per module).

import type { JSONContent } from "@tiptap/react";

// Optional: keep JSONContent available in your types layer.
export type TiptapJSONContent = JSONContent;

// Minimal JSON types (no JsonElement/JsonNull exports)
export type JsonPrimitive = string | number | boolean | null;
export type JsonValue = JsonPrimitive | JsonObject | JsonArray;
export interface JsonObject { [key: string]: JsonValue; }
export interface JsonArray extends Array<JsonValue> {}

export interface RichTextDocument {
  content: JsonObject;
}

export interface PagedResult<T> {
  items: T[];
  limit: number;
  offset: number;
  total: number;
  hasMore: boolean;
  nextOffset?: number | null;
}
""".trimIndent()
}
