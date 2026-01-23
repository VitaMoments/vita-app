package eu.vitamoments.app.tsgen

import java.io.File
import java.time.Instant

/**
 * Single entrypoint: generates multiple .ts files automatically by scanning packages.
 *
 * Usage:
 *   GenerateAllTsKt <outputDir>
 */
fun main(args: Array<String>) {
    val outDir = File(args.firstOrNull() ?: error("Usage: GenerateAllTsKt <outputDir>"))
    outDir.mkdirs()

    val timestamp = Instant.now().toString()

    // ---- Scan Kotlin packages -> "modules" -> output files ----
    // DTO modules are auto-discovered: eu.vitamoments.app.data.models.dto.<module>.*
    val dtoRoot = "eu.vitamoments.app.data.models.dto"
    val enumRoot = "eu.vitamoments.app.data.enums"
    val richtextRoot = "eu.vitamoments.app.data.models.domain.richtext"

    val modules = TsScan.discoverModules(
        dtoRoot = dtoRoot,
        extraModules = listOf(
            TsScan.ModuleSpec(name = "enums", packagePrefix = enumRoot),
            TsScan.ModuleSpec(name = "richtext", packagePrefix = richtextRoot),
        )
    )

    // Ownership map: symbol -> moduleName (used for stripping + imports)
    val ownership = TsScan.buildSymbolOwnership(modules)

    // Generate each module file
    modules.forEach { module ->
        val serializers = TsScan.resolveSerializers(module.classes)

        // If package contains no serializable concrete types (or only generics), skip
        if (serializers.isEmpty()) return@forEach

        val rawTs = TsGenerator.generate(serializers)

        // Strip anything "owned" by other modules and import those symbols instead
        val stripSymbols = ownership
            .filterValues { it != module.name }
            .keys
            .toList()

        val imports = stripSymbols
            .mapNotNull { sym ->
                val owner = ownership[sym] ?: return@mapNotNull null
                if (owner == module.name) return@mapNotNull null
                TsPostProcess.Import(symbol = sym, from = "./$owner")
            }

        val processed = TsPostProcess.process(
            rawTs = rawTs,
            headerDate = timestamp,
            imports = imports,
            stripExports = stripSymbols
        )

        File(outDir, "${module.name}.ts").writeText(processed)
    }

    // Barrel export
    val index = buildString {
        appendLine("// GENERATED FILE - DO NOT EDIT")
        appendLine("// Date: $timestamp")
        appendLine()
        modules
            .map { it.name }
            .distinct()
            .sorted()
            .forEach { name ->
                appendLine("""export * from "./$name";""")
            }
    }
    File(outDir, "index.ts").writeText(index)
}


//package eu.vitamoments.app.tsgen
//
//import eu.vitamoments.app.data.models.dto.feed.FeedItemDto
//import eu.vitamoments.app.data.models.dto.user.UserDto
//import eu.vitamoments.app.data.models.domain.richtext.RichTextDocument
//import java.io.File
//import java.time.Instant
//import kotlin.system.exitProcess
//
///**
// * Single entry point that generates multiple TypeScript files.
// *
// * Usage:
// *   GenerateAllTsKt <outputDir>
// */
//fun main(args: Array<String>) {
//    val outDirPath = args.firstOrNull() ?: run {
//        System.err.println("Usage: GenerateAllTsKt <output-dir>")
//        exitProcess(1)
//    }
//
//    val outDir = File(outDirPath).apply { mkdirs() }
//
//    val timestamp = Instant.now().toString()
//
//    // ---- MODULES (match your folder intent) ----
//    // feed.ts: root is FeedItemDto; imports UserDto + RichTextDocument
//    val feedRaw = TsGenerator.generate(listOf(FeedItemDto.serializer()))
//    val feedTs = TsPostProcess.process(
//        rawTs = feedRaw,
//        headerDate = timestamp,
//        imports = listOf(
//            TsPostProcess.Import("UserDto", "./user"),
//            TsPostProcess.Import("RichTextDocument", "./richtext"),
//        ),
//        stripExports = listOf(
//            // remove definitions from feed.ts (we import them)
//            "UserDto",
//            "FriendshipDto", // if it appears under UserDto dependencies
//            "UserRole",
//            "FriendshipStatus",
//            "FriendshipDirection",
//            "RichTextDocument",
//            "JsonElement",
//            "JsonNull",
//        )
//    )
//    File(outDir, "feed.ts").writeText(feedTs)
//
//    // user.ts: root is UserDto (includes FriendshipDto, UserRole, etc.)
//    val userRaw = TsGenerator.generate(listOf(UserDto.serializer()))
//    val userTs = TsPostProcess.process(
//        rawTs = userRaw,
//        headerDate = timestamp,
//        imports = emptyList(),
//        stripExports = emptyList()
//    )
//    File(outDir, "user.ts").writeText(userTs)
//
//    // richtext.ts: root is RichTextDocument
//    val richRaw = TsGenerator.generate(listOf(RichTextDocument.serializer()))
//    val richTs = TsPostProcess.process(
//        rawTs = richRaw,
//        headerDate = timestamp,
//        imports = emptyList(),
//        stripExports = emptyList()
//    )
//    File(outDir, "richtext.ts").writeText(richTs)
//}
