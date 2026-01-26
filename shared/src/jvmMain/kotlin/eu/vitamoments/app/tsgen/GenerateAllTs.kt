// GenerateAll.kt
package eu.vitamoments.app.tsgen

import java.io.File
import java.time.Instant
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val outDirPath = args.firstOrNull() ?: run {
        System.err.println("Usage: GenerateAllTsKt <output-dir>")
        exitProcess(1)
    }

    val outDir = File(outDirPath).apply { mkdirs() }
    val timestamp = Instant.now().toString()

    val contractsRoot = TsConfig.contractsRoot

    val discovered = TsScan.discoverDomainModules(contractsRoot).toMutableList()

    // Ensure required modules exist even if empty (common.ts needed for manual types)
    TsConfig.requiredModules.forEach { required ->
        if (discovered.none { it.name == required }) {
            discovered += TsScan.DomainModule(required, emptyList())
        }
    }

    val modules = discovered.distinctBy { it.name }.sortedBy { it.name }

    // Clean generated .ts files (including typeGuards.ts)
    outDir.listFiles()
        ?.filter { it.isFile && it.extension == "ts" }
        ?.forEach { it.delete() }

    val baseOwnership = TsScan.buildSymbolOwnership(modules)
    val ownership = TsOwnership.applyOverrides(baseOwnership)

    // Keep final TS per module in memory so we can generate typeGuards.ts afterwards
    val finalByModule = linkedMapOf<String, String>()

    modules.forEach { module ->
        val serializers = TsScan.resolveSerializers(module.classes)
        val rawTs = if (serializers.isEmpty()) "" else TsGenerator.generate(serializers)

        val processed = TsPostProcess.process(
            rawTs = rawTs,
            headerDate = timestamp,
            ownership = ownership,
            currentModule = module.name
        )

        val finalTs = TsManual.appendManuals(
            moduleName = module.name,
            ts = processed
        ).trimStart().trimEnd() + "\n"

        // Always write common.ts, others only if non-empty
        if (finalTs.isNotBlank() || module.name in TsConfig.alwaysWriteModules) {
            File(outDir, "${module.name}.ts").writeText(finalTs)
            finalByModule[module.name] = finalTs
        }
    }

    // ✅ Generate typeGuards.ts from the final outputs
    val typeGuardsTs = TsTypeGuardsGen.generate(
        headerDate = timestamp,
        moduleTsByName = finalByModule
    )
    File(outDir, "typeGuards.ts").writeText(typeGuardsTs)

    // ✅ index.ts includes typeGuards export
    val index = buildString {
        appendLine("// GENERATED FILE - DO NOT EDIT")
        appendLine("// Date: $timestamp")
        appendLine()
        modules.map { it.name }.distinct().sorted().forEach { name ->
            appendLine("""export * from "./$name";""")
        }
        appendLine("""export * from "./typeGuards";""")
    }
    File(outDir, "index.ts").writeText(index)
}
