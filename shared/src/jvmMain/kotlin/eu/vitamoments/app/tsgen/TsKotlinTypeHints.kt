package eu.vitamoments.app.tsgen

import eu.vitamoments.app.data.models.domain.common.PagedResult
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.withNullability

object TsKotlinTypeHints {

    /**
     * Returns: ClassName -> (propertyName -> typeArgSimpleName)
     *
     * Example:
     * UserWithContext.blogs -> BlogItem
     * UserWithContext.timeline -> TimelineItem
     */
    fun collectPagedResultTypeArgs(classes: List<Class<*>>): Map<String, Map<String, String>> {
        val out = linkedMapOf<String, MutableMap<String, String>>()

        for (clazz in classes) {
            val kClass = runCatching { clazz.kotlin }.getOrNull() ?: continue
            val ownerName = clazz.simpleName ?: continue

            for (p in kClass.memberProperties) {
                val rt = runCatching { p.returnType }.getOrNull() ?: continue
                val nonNull = runCatching { rt.withNullability(false) }.getOrNull() ?: rt

                val raw = nonNull.classifier as? KClass<*> ?: continue
                if (raw.qualifiedName != PagedResult::class.qualifiedName) continue

                val argType = nonNull.arguments.firstOrNull()?.type ?: continue
                val argKClass = argType.classifier as? KClass<*> ?: continue
                val argName = argKClass.simpleName ?: continue

                out.getOrPut(ownerName) { linkedMapOf() }[p.name] = TsOwnership.normalizeSymbol(argName)
            }
        }

        return out
    }
}