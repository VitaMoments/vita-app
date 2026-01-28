package eu.vitamoments.app.tsgen

import io.github.classgraph.ClassGraph
import kotlinx.serialization.Serializable
import kotlin.reflect.full.createType
import kotlinx.serialization.SerialName
import kotlin.reflect.full.findAnnotation

object TsScan {

    data class DomainModule(
        val name: String,                 // e.g. "feed", "user", "friend", "common", "enums"
        val classes: List<Class<*>>
    )

    /**
     * New module rules for SSOT under eu.vitamoments.app.data.models.*:
     *
     * - ... .enums.*                 -> enums.ts
     * - ... .common.*                -> common.ts
     *
     * - ... .domain.<module>.*       -> <module>.ts
     * - ... .requests.<module>.*     -> <module>.ts
     * - ... .responses.<module>.*    -> <module>.ts
     *
     * - ... .domain.* (no module)    -> common.ts
     * - any other top-level folder   -> that folder name as module
     */
    fun discoverDomainModules(
        contractsRoot: String
    ): List<DomainModule> {
        val serializableClasses = scanSerializableClasses(contractsRoot)

        val grouped = serializableClasses.groupBy { clazz ->
            val pkg = clazz.packageName
            val rest = pkg.removePrefix("$contractsRoot.") // e.g. "domain.user", "enums", "requests.feed"
            val segments = rest.split('.')

            val first = segments.getOrNull(0) ?: return@groupBy "common"

            when (first) {
                "enums" -> "enums"
                "common" -> "common"

                // ✅ NEW: treat domain like old models bucket
                "domain", "requests", "responses" -> segments.getOrNull(1) ?: "common"

                // ✅ any other top-level folder becomes module
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

    fun buildSymbolOwnership(modules: List<TsScan.DomainModule>): Map<String, String> {
        val map = linkedMapOf<String, String>()
        for (m in modules) {
            for (c in m.classes) {
                val k = runCatching { c.kotlin }.getOrNull()

                // 1) default: Kotlin class simpleName (UserWithContext, BlogItem, etc.)
                val simple = c.simpleName
                if (!simple.isNullOrBlank()) {
                    map.putIfAbsent(simple, m.name)
                }

                // 2) ✅ ALSO: @SerialName("BLOGITEM") -> "BLOGITEM"
                val serialName = k?.findAnnotation<SerialName>()?.value
                if (!serialName.isNullOrBlank()) {
                    map.putIfAbsent(serialName, m.name)
                }
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
