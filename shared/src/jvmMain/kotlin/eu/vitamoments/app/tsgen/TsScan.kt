package eu.vitamoments.app.tsgen

import io.github.classgraph.ClassGraph
import kotlinx.serialization.Serializable
import kotlin.reflect.full.createType

object TsScan {

    data class DomainModule(
        val name: String,                 // e.g. "feed", "user", "friendship", "common", "enums"
        val classes: List<Class<*>>
    )

    /**
     * Module rules (for contractsRoot = eu.vitamoments.app.data.models):
     *
     * - data.models.enums.*                     -> enums.ts
     * - data.models.common.*                    -> common.ts
     *
     * - data.models.domain.<domain>.*           -> <domain>.ts
     *   e.g. data.models.domain.user.*          -> user.ts
     *        data.models.domain.feed.*          -> feed.ts
     *
     * - data.models.domain.common.*             -> common.ts (optional)
     *
     * - Any other top-level folder becomes its own module:
     *   e.g. data.models.events.*               -> events.ts
     */
    fun discoverDomainModules(contractsRoot: String): List<DomainModule> {
        val serializableClasses = scanSerializableClasses(contractsRoot)

        val grouped = serializableClasses.groupBy { clazz ->
            val pkg = clazz.packageName

            val rest = pkg.removePrefix("$contractsRoot.")
            val segments = rest.split('.').filter { it.isNotBlank() }

            val first = segments.getOrNull(0) ?: return@groupBy "common"

            when (first) {
                "enums" -> "enums"
                "common" -> "common"

                // ✅ your new structure
                "domain", "requests", "responses" -> {
                    val second = segments.getOrNull(1) ?: "common"
                    // allow: domain.common.* -> common.ts
                    if (second == "common") "common" else second
                }

                // fallback: treat any other top-level folder as its own module
                else -> first
            }
        }

        return grouped
            .map { (name, classes) -> DomainModule(name, classes) }
            .sortedBy { it.name }
    }

    private fun scanSerializableClasses(packagePrefix: String): List<Class<*>> {
        val scan = ClassGraph()
            .enableClassInfo()
            .enableAnnotationInfo()
            .acceptPackages(packagePrefix)
            .scan()

        scan.use { result ->
            return result
                .getClassesWithAnnotation(Serializable::class.qualifiedName)
                .filter { it.packageName.startsWith(packagePrefix) }
                .map { it.loadClass() }
        }
    }

    /**
     * Ownership map: symbol simpleName -> module (feed/user/common/enums/etc.)
     * Used for stripping + auto imports.
     */
    fun buildSymbolOwnership(modules: List<DomainModule>): Map<String, String> {
        val map = linkedMapOf<String, String>()
        for (m in modules) {
            for (c in m.classes) {
                val name = c.simpleName ?: continue
                map.putIfAbsent(name, m.name)
            }
        }
        return map
    }

    /**
     * Resolve KSerializer instances for classes.
     * Skips generics.
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
