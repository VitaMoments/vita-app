package eu.vitamoments.app.tsgen

import io.github.classgraph.ClassGraph
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlin.reflect.full.createType

object TsTrace {

    /**
     * Print all classes under [contractsRoot] that match the given simpleName (e.g. "UserRole"),
     * including package name and classpath location (jar/file).
     */
    fun printBySimpleName(
        contractsRoot: String,
        simpleName: String
    ) {
        val target = simpleName.trim()
        require(target.isNotBlank()) { "simpleName must not be blank" }

        val scan = ClassGraph()
            .enableClassInfo()
            .acceptPackages(contractsRoot)
            .scan()

        scan.use { result ->
            val hits = result.allClasses
                .filter { it.name.endsWith(".$target") } // fully-qualified ends with ".UserRole"
                .sortedBy { it.name }

            println("[tsgen][trace] Search simpleName='$target' under '$contractsRoot' -> ${hits.size} hit(s)")

            if (hits.isEmpty()) return

            hits.forEachIndexed { idx, ci ->
                val loc = runCatching { ci.classpathElementURI?.toString() }.getOrNull()
                val src = runCatching { ci.resource?.url?.toString() }.getOrNull()

                println(
                    buildString {
                        appendLine("  #${idx + 1}")
                        appendLine("    fqcn     : ${ci.name}")
                        appendLine("    package  : ${ci.packageName}")
                        appendLine("    location : ${loc ?: "<unknown>"}")
                        appendLine("    resource : ${src ?: "<unknown>"}")
                    }.trimEnd()
                )
            }
        }
    }

    /**
     * Print all duplicate simpleNames under [contractsRoot].
     * Useful when you suspect multiple symbols collide and ownership becomes unstable.
     */
    fun printDuplicateSimpleNames(
        contractsRoot: String
    ) {
        val scan = ClassGraph()
            .enableClassInfo()
            .acceptPackages(contractsRoot)
            .scan()

        scan.use { result ->
            val bySimple = result.allClasses
                .groupBy { it.simpleName }

            val dups = bySimple
                .filter { (_, list) -> list.size > 1 }
                .toSortedMap()

            println("[tsgen][trace] Duplicate simpleNames under '$contractsRoot' -> ${dups.size} name(s)")

            dups.forEach { (name, list) ->
                println(" - $name (${list.size})")
                list.sortedBy { it.name }.forEach { ci ->
                    val loc = runCatching { ci.classpathElementURI?.toString() }.getOrNull()
                    println("    • ${ci.name} @ ${loc ?: "<unknown>"}")
                }
            }
        }
    }

    fun traceInstantFields(vararg classes: Class<*>) {
        classes.forEach { clazz ->
            val kClass = clazz.kotlin
            val serializer = kotlinx.serialization.serializer(kClass.createType()) as KSerializer<*>
            println("[tsgen][instant] ${clazz.name} root kind=${serializer.descriptor.kind}")
            printDescriptorTree(serializer.descriptor, indent = "  ", onlyAtFields = true)
            println()
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun printDescriptorTree(
        d: SerialDescriptor,
        indent: String,
        onlyAtFields: Boolean
    ) {
        // SEALED: elementen zijn varianten (subtypes)
        for (i in 0 until d.elementsCount) {
            val name = d.getElementName(i)
            val child = d.getElementDescriptor(i)

            if (d.kind is PolymorphicKind.SEALED) {
                println("${indent}- variant[$i] name='$name' serialName='${child.serialName}' kind=${child.kind}")
                // ga door in subtype om createdAt/updatedAt te zien
                printDescriptorTree(child, indent + "  ", onlyAtFields)
                continue
            }

            // CLASS: normale velden
            val looksLikeAt = name.contains("At", ignoreCase = true)
            if (!onlyAtFields || looksLikeAt) {
                println(
                    "${indent}- field '$name' => serialName='${child.serialName}' kind=${child.kind}"
                )
            }

            // doorlopen in nested classes (bv user: User.CONTEXT)
            if (child.kind is StructureKind.CLASS || child.kind is PolymorphicKind.SEALED) {
                printDescriptorTree(child, indent + "  ", onlyAtFields)
            }
        }
    }
}