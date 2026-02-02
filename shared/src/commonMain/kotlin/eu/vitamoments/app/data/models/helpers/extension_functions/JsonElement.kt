package eu.vitamoments.app.data.models.helpers.extension_functions

import kotlinx.serialization.json.*

fun JsonElement.isBlankRichText(): Boolean {
    // Accept both:
    // 1) direct array of blocks  (content = [ ... ])
    // 2) full doc object         (document = { type:"doc", content:[...] })
    val blocks: JsonArray? = when (this) {
        is JsonArray -> this
        is JsonObject -> this["content"] as? JsonArray
        else -> null
    }

    if (blocks == null || blocks.isEmpty()) return true

    // If there are text nodes, consider non-blank.
    fun hasNonBlankText(el: JsonElement): Boolean {
        return when (el) {
            is JsonObject -> {
                val text = el["text"]?.jsonPrimitive?.contentOrNull
                if (!text.isNullOrBlank()) return true
                val inner = el["content"] as? JsonArray ?: return false
                inner.any(::hasNonBlankText)
            }
            is JsonArray -> el.any(::hasNonBlankText)
            else -> false
        }
    }

    return !hasNonBlankText(blocks)
}
