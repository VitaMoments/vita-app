package eu.vitamoments.kxstsgen

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import java.io.File
import java.time.Instant
import java.time.format.DateTimeFormatter

class KxsTsGenProcessor(
    private val logger: KSPLogger,
    private val options: Map<String, String>
) : SymbolProcessor {

    private val basePackage: String = options["tsgen.basePackage"]
        ?: "eu.vitamoments.app.data.models"

    private val outDir: String = options["tsgen.outDir"]
        ?: errorOpt("Missing required KSP option: tsgen.outDir")

    private val discriminatorKey: String = options["tsgen.discriminatorKey"] ?: "type"

    private val includeRoots = setOf("domain", "enums", "requests", "responses")

    private val serializableFqn = "kotlinx.serialization.Serializable"
    private val serialNameFqn = "kotlinx.serialization.SerialName"

    private val uuidFqns = setOf("kotlin.uuid.Uuid")
    private val instantFqns = setOf("kotlin.time.Instant", "kotlinx.datetime.Instant")

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (outDir.isBlank()) return emptyList()

        // Pak alles met @Serializable
        val allSerializable = resolver
            .getSymbolsWithAnnotation(serializableFqn)
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.validate() }
            .toList()

        val candidates = allSerializable
            .filter { it.packageName.asString().startsWith("$basePackage.") }
            .filter { isInIncludedRoots(it.packageName.asString()) }
            .toList()

        // Module grouping
        val modules = linkedMapOf<String, MutableList<KSClassDeclaration>>()
        for (decl in candidates) {
            val mod = moduleNameFor(decl.packageName.asString()) ?: continue
            modules.getOrPut(mod) { mutableListOf() }.add(decl)
        }

        val exportNameResolver = ExportNameResolver(basePackage, logger)
        val moduleExportNames = modules.mapValues { (_, decls) ->
            exportNameResolver.computeExportNames(decls)
        }

        val generatedModuleNames = mutableSetOf<String>()

        // Generate each module .ts
        for ((mod, decls) in modules.toSortedMap()) {
            val emitter = ModuleEmitter(
                moduleName = mod,
                basePackage = basePackage,
                discriminatorKey = discriminatorKey,
                logger = logger,
                exportNames = moduleExportNames.getValue(mod),
                moduleNameForPackage = ::moduleNameFor,
                serialNameFqn = serialNameFqn,
                uuidFqns = uuidFqns,
                instantFqns = instantFqns
            )
            val text = emitter.emitModuleFile(decls)
            writeText(File(outDir, "$mod.ts"), text)
            generatedModuleNames.add(mod)
        }

        // Generate json.ts (shared JSON types used for kotlinx.serialization.json.*)
        val jsonTs = JsonEmitter().emit()
        writeText(File(outDir, "json.ts"), jsonTs)
        generatedModuleNames.add("json")

        // Generate typeGuards.ts
        val sealedRoots = candidates
            .filter { it.modifiers.contains(Modifier.SEALED) }
            .sortedBy { it.qualifiedName?.asString() ?: it.simpleName.asString() }

        val tg = TypeGuardsEmitter(
            basePackage = basePackage,
            discriminatorKey = discriminatorKey,
            moduleNameForPackage = ::moduleNameFor,
            serialNameFqn = serialNameFqn
        ).emit(sealedRoots)

        writeText(File(outDir, "typeGuards.ts"), tg)
        generatedModuleNames.add("typeGuards")

        // Generate index.ts
        val index = IndexEmitter().emit(generatedModuleNames.toList())
        writeText(File(outDir, "index.ts"), index)

        return emptyList()
    }

    private fun errorOpt(msg: String): String {
        logger.error(msg)
        return ""
    }

    private fun isInIncludedRoots(pkg: String): Boolean {
        val rest = pkg.removePrefix("$basePackage.")
        val root = rest.substringBefore('.')
        return root in includeRoots
    }

    /**
     * Package -> output module file name (zonder .ts)
     *
     * basePackage = eu.vitamoments.app.data.models
     * roots:
     * - domain.<scope>.* -> <scope>.ts
     * - enums.* -> enums.ts
     * - requests.<scope>.* -> <scope>_requests.ts
     * - responses.<scope>.* -> <scope>_responses.ts
     */
    private fun moduleNameFor(pkg: String): String? {
        if (!pkg.startsWith("$basePackage.")) return null
        val rest = pkg.removePrefix("$basePackage.")
        val segments = rest.split('.')
        if (segments.isEmpty()) return null

        val root = segments[0]
        if (root !in includeRoots) return null

        return when (root) {
            "enums" -> "enums"
            "domain" -> segments.getOrNull(1) ?: "domain"
            "requests" -> (segments.getOrNull(1) ?: "requests") + "_requests"
            "responses" -> (segments.getOrNull(1) ?: "responses") + "_responses"
            else -> null
        }
    }

    private fun writeText(file: File, content: String) {
        file.parentFile.mkdirs()
        file.writeText(content)
        logger.info("kxs-ts-gen wrote ${file.absolutePath}")
    }

    // ---------- helpers ----------

    private class IndexEmitter {
        fun emit(modules: List<String>): String {
            val now = DateTimeFormatter.ISO_INSTANT.format(Instant.now())

            val unique = modules
                .filter { it.isNotBlank() && it != "index" }
                .distinct()

            val domain = unique
                .filter { it != "enums" && it != "typeGuards" && !it.endsWith("_requests") && !it.endsWith("_responses") }
                .sorted()

            val enums = unique.filter { it == "enums" }
            val req = unique.filter { it.endsWith("_requests") }.sorted()
            val res = unique.filter { it.endsWith("_responses") }.sorted()
            val tg = unique.filter { it == "typeGuards" }

            val all = domain + enums + req + res + tg

            return buildString {
                appendLine("// GENERATED FILE - DO NOT EDIT")
                appendLine("// Date: $now")
                appendLine()
                for (m in all) appendLine("export * from \"./$m\";")
            }
        }
    }

    /**
     * Option A: disambiguation bij collisions in dezelfde output file:
     * we prefixen met package tail na root+scope.
     */
    private class ExportNameResolver(
        private val basePackage: String,
        private val logger: KSPLogger
    ) {
        fun computeExportNames(decls: List<KSClassDeclaration>): Map<KSClassDeclaration, String> {
            val base = decls.associateWith { it.simpleName.asString() }.toMutableMap()
            val collisions = base.entries.groupBy { it.value }.filterValues { it.size > 1 }

            for ((name, entries) in collisions) {
                for ((decl, _) in entries) {
                    val pkg = decl.packageName.asString()
                    val rest = pkg.removePrefix("$basePackage.")
                    val segs = rest.split('.')
                    // segs: root, scope, tail...
                    val tail = segs.drop(2)
                    val prefix = tail.joinToString("") { it.toPascal() }
                    val newName = (prefix + name).ifBlank { name }
                    base[decl] = newName
                    logger.warn("Name collision for '$name'. Using '$newName' for ${decl.qualifiedName?.asString()}")
                }
            }

            // als het alsnog collide -> nummer suffix
            val again = base.entries.groupBy { it.value }.filterValues { it.size > 1 }
            for ((n, entries) in again) {
                entries.forEachIndexed { idx, (decl, _) ->
                    base[decl] = "${n}_${idx + 1}"
                }
            }
            return base
        }
    }

    private class ModuleEmitter(
        private val moduleName: String,
        private val basePackage: String,
        private val discriminatorKey: String,
        private val logger: KSPLogger,
        private val exportNames: Map<KSClassDeclaration, String>,
        private val moduleNameForPackage: (String) -> String?,
        private val serialNameFqn: String,
        private val uuidFqns: Set<String>,
        private val instantFqns: Set<String>
    ) {

        fun emitModuleFile(decls: List<KSClassDeclaration>): String {
            val now = DateTimeFormatter.ISO_INSTANT.format(Instant.now())

            val imports = linkedMapOf<String, MutableSet<String>>() // module -> type names

            val enums = decls.filter { it.classKind == ClassKind.ENUM_CLASS }
            val nonEnums = decls.filter { it.classKind != ClassKind.ENUM_CLASS }

            val sealedRoots = nonEnums.filter { it.modifiers.contains(Modifier.SEALED) }
            val leafTypes = nonEnums.filter { it !in sealedRoots }

            val out = StringBuilder()

            // Enums: prefer string unions (simpel, tree-shakeable)
            // Enums: generate runtime const-object + type union (so you can use BlogCategory.MENTAL)
            if (moduleName == "enums") {
                for (e in enums.sortedBy { it.simpleName.asString() }) {
                    val exportName = exportNames[e] ?: e.simpleName.asString()

                    val values = e.declarations
                        .filterIsInstance<KSClassDeclaration>()
                        .filter { it.classKind == ClassKind.ENUM_ENTRY }
                        .map { it.simpleName.asString() }
                        .distinct()
                        .toList()

                    // Runtime value object
                    out.appendLine("export const $exportName = {")
                    for (v in values) {
                        out.appendLine("  $v: \"$v\",")
                    }
                    out.appendLine("} as const;")

                    // Type union
                    out.appendLine("export type $exportName = typeof $exportName[keyof typeof $exportName];")
                    out.appendLine()
                }
            }

            // Leaf types -> interfaces
            for (d in leafTypes.sortedBy { it.qualifiedName?.asString() ?: it.simpleName.asString() }) {
                if (d.classKind != ClassKind.CLASS && d.classKind != ClassKind.INTERFACE) continue
                emitInterface(d, exportNames[d] ?: d.simpleName.asString(), imports, out)
                out.appendLine()
            }

            // Sealed roots -> namespace + union
            for (root in sealedRoots.sortedBy { it.qualifiedName?.asString() ?: it.simpleName.asString() }) {
                val rootName = exportNames[root] ?: root.simpleName.asString()
                emitSealedRoot(root, rootName, imports, out)
                out.appendLine()
            }

            val importText = buildImports(imports)

            return buildString {
                appendLine("// GENERATED FILE - DO NOT EDIT")
                appendLine("// Source: Kotlin @Serializable models (kxs-ts-gen)")
                appendLine("// Date: $now")
                appendLine()
                if (importText.isNotBlank()) {
                    appendLine(importText)
                    appendLine()
                }
                append(out.toString().trimEnd())
                appendLine()
            }
        }

        private fun emitInterface(
            decl: KSClassDeclaration,
            exportName: String,
            imports: LinkedHashMap<String, MutableSet<String>>,
            out: StringBuilder
        ) {
            val ctor = decl.primaryConstructor
            val props = ctor?.parameters ?: emptyList()

            val declFqn = decl.qualifiedName?.asString()

            val typeParams = decl.typeParameters.map { it.name.asString() }
            val generics = if (typeParams.isNotEmpty()) "<${typeParams.joinToString(", ")}>" else ""

            out.appendLine("export interface $exportName$generics {")

            for (p in props) {
                val name = p.name?.asString() ?: continue
                val resolved = p.type.resolve()
                val isNullable = resolved.nullability == Nullability.NULLABLE
                val optional = p.hasDefault || isNullable
                val opt = if (optional) "?" else ""

                // ✅ Only for RichTextDocument.content -> JSONContent
                if (
                    declFqn == "eu.vitamoments.app.data.models.domain.common.RichTextDocument" &&
                    name == "content"
                ) {
                    imports.getOrPut("@tiptap/core") { linkedSetOf() }.add("JSONContent")
                    val ts = if (isNullable) "JSONContent | null" else "JSONContent"
                    out.appendLine("  $name$opt: $ts;")
                    continue
                }

                val override = serializerOverrideTsCoreType(p)
                val tsType =
                    if (override != null) {
                        if (isNullable) "$override | null" else override
                    } else {
                        toTsType(resolved, imports)
                    }

                out.appendLine("  $name$opt: $tsType;")
            }

            out.appendLine("}")
        }


        private fun emitSealedRoot(
            root: KSClassDeclaration,
            rootExportName: String,
            imports: LinkedHashMap<String, MutableSet<String>>,
            out: StringBuilder
        ) {
            val subclasses = root.getSealedSubclasses()
                .filter { it.validate() }
                .toList()
                .sortedBy { it.qualifiedName?.asString() ?: it.simpleName.asString() }

            if (subclasses.isEmpty()) {
                out.appendLine("export type $rootExportName = never;")
                return
            }

            // root module import for root type (used in other modules) is handled by callers
            val variants = subclasses.map { sub ->
                val serial = serialNameOf(sub) ?: sub.simpleName.asString()
                val enumKey = serial.toEnumKey()

                // Ensure subtype interface is importable if in another module
                val subMod = moduleNameForPackage(sub.packageName.asString())
                val subTypeName = exportNames[sub] ?: sub.simpleName.asString()
                if (subMod != null && subMod != moduleName) {
                    imports.getOrPut(subMod) { linkedSetOf() }.add(subTypeName)
                }

                Variant(subTypeName, serial, enumKey)
            }

            out.appendLine("export namespace $rootExportName {")
            out.appendLine("  export enum Type {")
            for (v in variants) {
                out.appendLine("    ${v.enumKey} = \"${v.serial}\",")
            }
            out.appendLine("  }")
            out.appendLine()
            for (v in variants) {
                out.appendLine("  export type ${v.enumKey} = { $discriminatorKey: Type.${v.enumKey} } & ${v.subExportName};")
            }
            out.appendLine("}")
            out.appendLine()
            out.appendLine("export type $rootExportName = ${variants.joinToString(" | ") { "$rootExportName.${it.enumKey}" }};")
        }

        private data class Variant(val subExportName: String, val serial: String, val enumKey: String)

        private fun serialNameOf(decl: KSClassDeclaration): String? {
            val ann = decl.annotations.firstOrNull {
                it.annotationType.resolve().declaration.qualifiedName?.asString() == serialNameFqn
            } ?: return null
            return ann.arguments.firstOrNull { it.name?.asString() == "value" }?.value as? String
        }

        private fun buildImports(imports: Map<String, Set<String>>): String {
            return imports.entries
                .filter { (mod, names) -> names.isNotEmpty() && mod != moduleName }
                .sortedBy { it.key }
                .joinToString("\n") { (mod, names) ->
                    val sorted = names.toList().distinct().sorted()

                    val fromPath =
                        if (mod.startsWith("@") || mod.startsWith("http")) mod
                        else "./$mod"

                    "import type { ${sorted.joinToString(", ")} } from \"$fromPath\";"
                }
        }


        private fun toTsType(type: KSType, imports: LinkedHashMap<String, MutableSet<String>>): String {
            val decl = type.declaration

            if (decl is KSTypeParameter) return decl.name.asString()

            val qn = (decl as? KSClassDeclaration)?.qualifiedName?.asString()
            val nullable = type.nullability == Nullability.NULLABLE

            val core = when {
                qn == "kotlin.String" -> "string"
                qn == "kotlin.Boolean" -> "boolean"
                qn in setOf("kotlin.Int", "kotlin.Long", "kotlin.Double", "kotlin.Float", "kotlin.Short") -> "number"
                qn in uuidFqns -> "string"
                qn in instantFqns -> "number"

                qn == "kotlinx.serialization.json.JsonElement" -> {
                    imports.getOrPut("json") { linkedSetOf() }.add("JsonValue")
                    "JsonValue"
                }

                qn == "kotlinx.serialization.json.JsonObject" -> {
                    imports.getOrPut("json") { linkedSetOf() }.add("JsonObject")
                    "JsonObject"
                }

                qn == "kotlinx.serialization.json.JsonArray" -> {
                    imports.getOrPut("json") { linkedSetOf() }.add("JsonArray")
                    "JsonArray"
                }

                qn == "kotlinx.serialization.json.JsonPrimitive" -> {
                    imports.getOrPut("json") { linkedSetOf() }.add("JsonPrimitive")
                    "JsonPrimitive"
                }

                qn?.startsWith("kotlinx.serialization.json.") == true && qn.substringAfterLast('.').startsWith("Json") -> {
                    // fallback: alle onbekende Json* -> JsonValue
                    imports.getOrPut("json") { linkedSetOf() }.add("JsonValue")
                    "JsonValue"
                }

                qn == "kotlinx.serialization.json.JsonNull" -> "null"

                qn in setOf(
                    "kotlin.collections.List",
                    "kotlin.collections.MutableList",
                    "kotlin.collections.Set",
                    "kotlin.collections.MutableSet"
                ) -> {
                    val arg = type.arguments.firstOrNull()?.type?.resolve()
                    val el = if (arg != null) toTsType(arg, imports) else "unknown"
                    val elWrapped = if (el.contains(" | ")) "($el)" else el
                    "$elWrapped[]"
                }

                qn in setOf("kotlin.collections.Map", "kotlin.collections.MutableMap") -> {
                    val k = type.arguments.getOrNull(0)?.type?.resolve()
                    val v = type.arguments.getOrNull(1)?.type?.resolve()
                    val kTs = if (k != null) toTsType(k, imports) else "unknown"
                    if (kTs != "string") {
                        logger.error("Unsupported Map key type for TS: $kTs. Only Map<String, V> is supported.")
                        "never"
                    } else {
                        val vTs = if (v != null) toTsType(v, imports) else "unknown"
                        "Record<string, $vTs>"
                    }
                }

                decl is KSTypeParameter -> decl.name.asString()

                decl is KSClassDeclaration && decl.classKind == ClassKind.ENUM_CLASS -> {
                    val name = decl.simpleName.asString()
                    imports.getOrPut("enums") { linkedSetOf() }.add(name)
                    name
                }

                decl is KSClassDeclaration -> {
                    val pkg = decl.packageName.asString()
                    val mod = moduleNameForPackage(pkg)
                    val name = decl.simpleName.asString()

                    if (mod != null && mod != moduleName) {
                        imports.getOrPut(mod) { linkedSetOf() }.add(name)
                    }

                    if (type.arguments.isNotEmpty()) {
                        val args = type.arguments.mapNotNull { it.type?.resolve() }.map { toTsType(it, imports) }
                        "$name<${args.joinToString(", ")}>"
                    } else name
                }

                else -> "unknown"
            }

            return if (nullable) "$core | null" else core
        }

        private fun serializerOverrideTsCoreType(p: KSValueParameter): String? {
            // Check: @Serializable(with = X::class)
            val ann = p.annotations.firstOrNull {
                it.annotationType.resolve().declaration.qualifiedName?.asString() == "kotlinx.serialization.Serializable"
            } ?: return null

            val withArg = ann.arguments.firstOrNull { it.name?.asString() == "with" }?.value ?: return null

            val serializerFqn = when (withArg) {
                is KSType -> (withArg.declaration as? KSClassDeclaration)?.qualifiedName?.asString()
                is KSTypeReference -> (withArg.resolve().declaration as? KSClassDeclaration)?.qualifiedName?.asString()
                is KSClassDeclaration -> withArg.qualifiedName?.asString()
                else -> null
            } ?: return null

            // Jouw case: InstantSerializer serialiseert naar Long epoch millis -> TS number
            return if (
                serializerFqn.endsWith(".InstantSerializer") ||
                serializerFqn.endsWith(".IntentSerializer") // voor typo/variant in je bericht
            ) {
                "number"
            } else null
        }
    }

    private class JsonEmitter {
        fun emit(): String = buildString {
            appendLine("// GENERATED FILE - DO NOT EDIT")
            appendLine()
            appendLine("export type JsonPrimitive = string | number | boolean | null;")
            appendLine("export type JsonArray = JsonValue[];")
            appendLine("export type JsonObject = { [key: string]: JsonValue };")
            appendLine("export type JsonValue = JsonPrimitive | JsonObject | JsonArray;")
            appendLine()
        }
    }


    private class TypeGuardsEmitter(
        private val basePackage: String,
        private val discriminatorKey: String,
        private val moduleNameForPackage: (String) -> String?,
        private val serialNameFqn: String
    ) {
        fun emit(sealedRoots: List<KSClassDeclaration>): String {
            val now = DateTimeFormatter.ISO_INSTANT.format(Instant.now())

            val imports = linkedMapOf<String, MutableSet<String>>()
            val body = StringBuilder()

            for (root in sealedRoots) {
                val mod = moduleNameForPackage(root.packageName.asString()) ?: continue
                val rootName = root.simpleName.asString()
                imports.getOrPut(mod) { linkedSetOf() }.add(rootName)

                val subs = root.getSealedSubclasses().filter { it.validate() }.toList()
                if (subs.isEmpty()) continue

                body.appendLine("/**")
                body.appendLine(" * -------------------------")
                body.appendLine(" * $rootName type guards")
                body.appendLine(" * -------------------------")
                body.appendLine(" */")

                val variants = subs.map { sub ->
                    val serial = serialNameOf(sub) ?: sub.simpleName.asString()
                    serial
                }.distinct().sorted()

                for (serial in variants) {
                    val fn = "is${rootName}${serial.toPascal()}"
                    val enumKey = serial.toEnumKey()
                    body.appendLine("export const $fn = (x: $rootName): x is $rootName.$enumKey => x.$discriminatorKey === $rootName.Type.$enumKey;")
                }
                body.appendLine()
            }

            val importText = imports.entries
                .sortedBy { it.key }
                .joinToString("\n") { (mod, names) ->
                    val sorted = names.toList().distinct().sorted()
                    "import { ${sorted.joinToString(", ")} } from \"./$mod\";"
                }

            return buildString {
                appendLine("// GENERATED FILE - DO NOT EDIT")
                appendLine("// Source: Kotlin @Serializable models (kxs-ts-gen)")
                appendLine("// Date: $now")
                appendLine()
                if (importText.isNotBlank()) {
                    appendLine(importText)
                    appendLine()
                }
                append(body.toString().trimEnd())
                appendLine()
            }
        }

        private fun serialNameOf(decl: KSClassDeclaration): String? {
            val ann = decl.annotations.firstOrNull {
                it.annotationType.resolve().declaration.qualifiedName?.asString() == serialNameFqn
            } ?: return null
            return ann.arguments.firstOrNull { it.name?.asString() == "value" }?.value as? String
        }
    }
}

// ---------- string utils ----------
private fun String.toPascal(): String =
    split('_', '-', '.', ' ')
        .filter { it.isNotBlank() }
        .joinToString("") { it.lowercase().replaceFirstChar { c -> c.uppercase() } }

private fun String.toEnumKey(): String =
    if (all { it.isUpperCase() || it == '_' || it.isDigit() }) this
    else this.toPascal().replace(Regex("([a-z0-9])([A-Z])"), "$1_$2").uppercase()
