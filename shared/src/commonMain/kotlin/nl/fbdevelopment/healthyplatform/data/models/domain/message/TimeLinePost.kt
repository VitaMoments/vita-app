@file:OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)

package nl.fbdevelopment.healthyplatform.data.models.domain.message

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import nl.fbdevelopment.healthyplatform.data.models.domain.user.PublicUser
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class TimeLinePost(
    val uuid: Uuid,
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Instant? = null,
    val createdBy: PublicUser,
    val content: JsonObject
) {
    val plainText: String
        get() = contentToPlainText(content)

    val html: String
        get() = contentToSafeHtml(content)

    private fun contentToPlainText(doc: JsonObject): String {
        val blocks = doc["content"]?.jsonArray ?: return ""
        val sb = StringBuilder()

        blocks.forEachIndexed { bi, blockEl ->
            val block = blockEl.jsonObject
            if (block["type"]?.jsonPrimitive?.contentOrNull != "paragraph") return@forEachIndexed

            val inlines = block["content"]?.jsonArray
            if (!inlines.isNullOrEmpty()) {
                inlines.forEach { inlineEl ->
                    val inline = inlineEl.jsonObject
                    when (inline["type"]?.jsonPrimitive?.contentOrNull) {
                        "text" -> sb.append(inline["text"]?.jsonPrimitive?.contentOrNull.orEmpty())
                        "hardBreak" -> sb.append('\n')
                    }
                }
            }

            if (bi != blocks.size - 1) sb.append('\n')
        }

        return sb.toString().trimEnd()
    }

    private fun contentToSafeHtml(doc: JsonObject): String {
        val blocks = doc["content"]?.jsonArray ?: return ""
        val sb = StringBuilder()

        blocks.forEach { blockEl ->
            val block = blockEl.jsonObject
            if (block["type"]?.jsonPrimitive?.contentOrNull != "paragraph") return@forEach

            sb.append("<p>")

            val inlines = block["content"]?.jsonArray
            if (inlines.isNullOrEmpty()) {
                // lege paragrafen zichtbaar houden
                sb.append("<br/>")
            } else {
                inlines.forEach { inlineEl ->
                    val inline = inlineEl.jsonObject
                    when (inline["type"]?.jsonPrimitive?.contentOrNull) {
                        "hardBreak" -> sb.append("<br/>")
                        "text" -> {
                            val raw = inline["text"]?.jsonPrimitive?.contentOrNull.orEmpty()
                            val text = escapeHtml(raw)

                            val marks = inline["marks"]?.jsonArray
                                ?.mapNotNull { it.jsonObject["type"]?.jsonPrimitive?.contentOrNull }
                                ?.toSet()
                                ?: emptySet()

                            // vaste volgorde zodat tags netjes nest-en
                            if ("bold" in marks) sb.append("<strong>")
                            if ("italic" in marks) sb.append("<em>")
                            if ("underline" in marks) sb.append("<u>")

                            sb.append(text)

                            if ("underline" in marks) sb.append("</u>")
                            if ("italic" in marks) sb.append("</em>")
                            if ("bold" in marks) sb.append("</strong>")
                        }
                    }
                }
            }

            sb.append("</p>")
        }

        return sb.toString()
    }

    private fun escapeHtml(s: String): String =
        buildString(s.length) {
            for (ch in s) {
                append(
                    when (ch) {
                        '<' -> "&lt;"
                        '>' -> "&gt;"
                        '&' -> "&amp;"
                        '"' -> "&quot;"
                        '\'' -> "&#39;"
                        else -> ch
                    }
                )
            }
        }
}

