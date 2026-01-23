package eu.vitamoments.app.tsgen

import java.lang.reflect.Array as JArray
import java.lang.reflect.Method

/**
 * Wraps kxs-ts-gen via reflection so we don't need compile-time dependency in jvmMain.
 */
object TsGenerator {

    fun generate(serializers: List<Any>): String {
        val genClass = Class.forName("dev.adamko.kxstsgen.KxsTsGenerator")
        val generator = genClass.getDeclaredConstructor().newInstance()

        val kSerializerClass = Class.forName("kotlinx.serialization.KSerializer")
        val serialDescriptorClass = Class.forName("kotlinx.serialization.descriptors.SerialDescriptor")

        val candidates = genClass.methods
            .filter { it.name == "generate" && it.parameterTypes.size == 1 && it.returnType == String::class.java }

        fun score(m: Method): Int {
            val p = m.parameterTypes[0]
            val isSerializer = kSerializerClass.isAssignableFrom(p)
            val isSerializerArray = p.isArray && kSerializerClass.isAssignableFrom(p.componentType)
            val isDescriptor = serialDescriptorClass.isAssignableFrom(p)
            return when {
                isSerializerArray -> 0  // best: generate(KSerializer[])
                isSerializer -> 1       // ok: generate(KSerializer)
                isDescriptor -> 2       // fallback: generate(SerialDescriptor)
                else -> 9
            }
        }

        val generateMethod = candidates.minByOrNull(::score)
            ?: error("Could not find a suitable KxsTsGenerator.generate(...) overload")

        val paramType = generateMethod.parameterTypes[0]
        val acceptsSerializer = kSerializerClass.isAssignableFrom(paramType)
        val acceptsSerializerArray = paramType.isArray && kSerializerClass.isAssignableFrom(paramType.componentType)
        val acceptsDescriptor = serialDescriptorClass.isAssignableFrom(paramType)

        val getDescriptorMethod = if (acceptsDescriptor) {
            kSerializerClass.getMethod("getDescriptor")
        } else null

        fun serializersArray(): Any {
            val component = paramType.componentType
            val arr = JArray.newInstance(component, serializers.size)
            serializers.forEachIndexed { i, s -> JArray.set(arr, i, s) }
            return arr
        }

        val arg: Any = when {
            acceptsSerializerArray -> serializersArray()
            acceptsSerializer -> serializers.first()
            acceptsDescriptor -> getDescriptorMethod!!.invoke(serializers.first())
            else -> error("Unsupported generate(...) parameter type: ${paramType.name}")
        }

        return generateMethod.invoke(generator, arg) as String
    }
}
