// TsManual.kt
package eu.vitamoments.app.tsgen

object TsManual {

    fun appendManuals(moduleName: String, ts: String): String {
        var out = ts.trimEnd()

        if (moduleName == "common") {
            out = injectAfterHeader(out, richTextDocumentTs())
        }

        // Hard guarantee: header ends up on top
        out = TsHeader.ensureHeaderFirst(out)

        return out.trimEnd() + "\n"
    }

    private fun injectAfterHeader(ts: String, insert: String): String {
        val split = TsHeader.split(ts)
        val manualBlock = insert.trimEnd() + "\n\n"

        return if (split.header == null) {
            // No header found: prepend manual, ensureHeaderFirst will reorder if header appears later
            manualBlock + ts.trimStart()
        } else {
            // header + manual + rest
            split.header.trimEnd() + "\n\n" + manualBlock + split.body.trimStart()
        }
    }

    fun richTextDocumentTs(): String = """
// ---- Manually emitted shared types ----
// RichTextDocument is used across many contracts, but should live in common.ts.

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
""".trimIndent()
}
