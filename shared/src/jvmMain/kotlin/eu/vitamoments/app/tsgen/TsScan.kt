package eu.vitamoments.app.tsgen

import io.github.classgraph.ClassGraph
import kotlinx.serialization.Serializable
import kotlin.reflect.full.createType

object TsScan {

    data class ModuleSpec(val name: String, val packagePrefix: String)

    data class Module(
        val name: String,
        val packagePrefix: String,
        val classes: List<Class<*>>
    )

    /**
     * Auto-discover DTO modules:
     *  - scans dtoRoot and groups by first segment after dtoRoot
     *    e.g. eu...dto.feed.* -> module "feed"
     *         eu...dto.recipe.* -> module "recipe"
     *
     * plus extraModules for things not under dtoRoot (e.g. enums, richtext).
     */
    fun discoverModules(
        dtoRoot: String,
        extraModules: List<ModuleSpec> = emptyList()
    ): List<Module> {
        val scan = ClassGraph()
            .enableClassInfo()
            .enableAnnotationInfo()
            .acceptPackages(dtoRoot)
            .scan()

        scan.use { result ->
            val serializable = result
                .getClassesWithAnnotation(Serializable::class.qualifiedName)
                .filter { it.packageName.startsWith(dtoRoot) }
                .map { it.loadClass() }

            val grouped = serializable.groupBy { clazz ->
                val pkg = clazz.packageName
                val rest = pkg.removePrefix("$dtoRoot.")
                rest.substringBefore('.') // first segment after dtoRoot
            }

            val dtoModules = grouped.map { (name, classes) ->
                Module(
                    name = name,
                    packagePrefix = "$dtoRoot.$name",
                    classes = classes
                )
            }

            // Extra modules (enums, richtext, etc.) scanned separately:
            val extras = extraModules.map { spec ->
                val extraScan = ClassGraph()
                    .enableClassInfo()
                    .enableAnnotationInfo()
                    .acceptPackages(spec.packagePrefix)
                    .scan()

                extraScan.use { r ->
                    val extraClasses = r
                        .getClassesWithAnnotation(Serializable::class.qualifiedName)
                        .filter { it.packageName.startsWith(spec.packagePrefix) }
                        .map { it.loadClass() }

                    Module(
                        name = spec.name,
                        packagePrefix = spec.packagePrefix,
                        classes = extraClasses
                    )
                }
            }

            return (dtoModules + extras)
                .distinctBy { it.name }
                .sortedBy { it.name }
        }
    }

    /**
     * Ownership is based on Kotlin simpleName -> moduleName.
     * Used to strip duplicates and generate imports.
     */
    fun buildSymbolOwnership(modules: List<Module>): Map<String, String> {
        val map = linkedMapOf<String, String>()
        for (m in modules) {
            for (c in m.classes) {
                val name = c.simpleName ?: continue
                // First win: stable deterministic order by module list
                map.putIfAbsent(name, m.name)
            }
        }
        return map
    }

    /**
     * Resolve KSerializer instances for classes.
     * We use kotlinx.serialization.serializer(KType) via reflection-free API.
     *
     * Skips:
     * - generic classes (typeParameters not empty)
     * - local/anonymous classes
     */
    fun resolveSerializers(classes: List<Class<*>>): List<Any> {
        val serializers = mutableListOf<Any>()
        for (clazz in classes) {
            val kClass = runCatching { clazz.kotlin }.getOrNull() ?: continue
            if (kClass.typeParameters.isNotEmpty()) continue

            val kType = runCatching { kClass.createType() }.getOrNull() ?: continue
            val serializer = runCatching { kotlinx.serialization.serializer(kType) }.getOrNull() ?: continue

            serializers += serializer
        }
        return serializers
    }
}
