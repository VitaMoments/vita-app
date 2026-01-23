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

    // clean old outputs
    outDir.listFiles()
        ?.filter { it.isFile && it.extension == "ts" }
        ?.forEach { it.delete() }

    val timestamp = Instant.now().toString()
    val contractsRoot = "eu.vitamoments.app.api.contracts"

    val modules = TsScan.discoverDomainModules(contractsRoot)
    val ownership = TsScan.buildSymbolOwnership(modules)

    modules.forEach { module ->
        val serializers = TsScan.resolveSerializers(module.classes)
        val rawTs = if (serializers.isEmpty()) "" else TsGenerator.generate(serializers)

        val processed = TsPostProcess.processAutoImports(
            rawTs = rawTs,
            headerDate = timestamp,
            ownership = ownership,
            currentModule = module.name
        )

        // Manual generic edge cases (optional)
        val hasPaged = module.classes.any { it.simpleName == "PagedResultContract" }

        val finalTs = buildString {
            append(processed.trim())
            if (hasPaged) {
                appendLine()
                appendLine()
                append(TsManual.pagedResultContractTs())
            }
            appendLine()
        }.trimStart()

        if (finalTs.isNotBlank()) {
            File(outDir, "${module.name}.ts").writeText(finalTs)
        }
    }

    // index.ts optional
    val index = buildString {
        appendLine("// GENERATED FILE - DO NOT EDIT")
        appendLine("// Date: $timestamp")
        appendLine()
        modules.map { it.name }.distinct().sorted().forEach { name ->
            appendLine("""export * from "./$name";""")
        }
    }
    File(outDir, "index.ts").writeText(index)
}
