package eu.vitamoments.app.tsgen

import eu.vitamoments.app.data.models.dto.feed.FeedItemDto
import eu.vitamoments.app.data.models.dto.user.UserDto
import eu.vitamoments.app.data.models.domain.richtext.RichTextDocument
import java.io.File
import java.time.Instant
import kotlin.system.exitProcess

/**
 * Single entry point that generates multiple TypeScript files.
 *
 * Usage:
 *   GenerateAllTsKt <outputDir>
 */
fun main(args: Array<String>) {
    val outDirPath = args.firstOrNull() ?: run {
        System.err.println("Usage: GenerateAllTsKt <output-dir>")
        exitProcess(1)
    }

    val outDir = File(outDirPath).apply { mkdirs() }

    val timestamp = Instant.now().toString()

    // ---- MODULES (match your folder intent) ----
    // feed.ts: root is FeedItemDto; imports UserDto + RichTextDocument
    val feedRaw = TsGenerator.generate(listOf(FeedItemDto.serializer()))
    val feedTs = TsPostProcess.process(
        rawTs = feedRaw,
        headerDate = timestamp,
        imports = listOf(
            TsPostProcess.Import("UserDto", "./user"),
            TsPostProcess.Import("RichTextDocument", "./richtext"),
        ),
        stripExports = listOf(
            // remove definitions from feed.ts (we import them)
            "UserDto",
            "FriendshipDto", // if it appears under UserDto dependencies
            "UserRole",
            "FriendshipStatus",
            "FriendshipDirection",
            "RichTextDocument",
            "JsonElement",
            "JsonNull",
        )
    )
    File(outDir, "feed.ts").writeText(feedTs)

    // user.ts: root is UserDto (includes FriendshipDto, UserRole, etc.)
    val userRaw = TsGenerator.generate(listOf(UserDto.serializer()))
    val userTs = TsPostProcess.process(
        rawTs = userRaw,
        headerDate = timestamp,
        imports = emptyList(),
        stripExports = emptyList()
    )
    File(outDir, "user.ts").writeText(userTs)

    // richtext.ts: root is RichTextDocument
    val richRaw = TsGenerator.generate(listOf(RichTextDocument.serializer()))
    val richTs = TsPostProcess.process(
        rawTs = richRaw,
        headerDate = timestamp,
        imports = emptyList(),
        stripExports = emptyList()
    )
    File(outDir, "richtext.ts").writeText(richTs)
}
