package eu.vitamoments.app.tsgen

import io.github.classgraph.ClassGraph
import kotlinx.serialization.Serializable
import kotlin.reflect.full.createType

object TsScan {

    data class DomainModule(
        val name: String,                 // e.g. "feed", "user", "friend", "common", "enums"
        val classes: List<Class<*>>
    )

    /**
     * Module rules:
     * - contracts.enums.*                  -> enums.ts
     * - contracts.models.<domain>.*        -> <domain>.ts
     * - contracts.requests.<domain>.*      -> <domain>.ts
     * - contracts.responses.<domain>.*     -> <domain>.ts
     * - contracts.common.* OR models.* w/o domain -> common.ts
     */
    fun discoverDomainModules(
        contractsRoot: String
    ): List<DomainModule> {
        val serializableClasses = scanSerializableClasses(contractsRoot)

        val grouped = serializableClasses.groupBy { clazz ->
            val pkg = clazz.packageName
            val rest = pkg.removePrefix("$contractsRoot.") // e.g. "models.feed", "enums", "common"
            val segments = rest.split('.')

            val first = segments.getOrNull(0) ?: return@groupBy "common"

            when (first) {
                // ✅ top-level module
                "enums" -> "enums"
                "common" -> "common"

                // ✅ domain modules under models/requests/responses
                "models", "requests", "responses" -> segments.getOrNull(1) ?: "common"

                // ✅ any other top-level folder becomes its own module name
                // (handig als je later contracts/events.* of contracts/utils.* wilt)
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
     * Ownership map: symbol simpleName -> module (feed/user/friend/common/enums)
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
