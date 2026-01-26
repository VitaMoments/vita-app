package eu.vitamoments.app.tsgen

object TsEnumGen {

    fun generateEnumsTs(
        headerDate: String,
        enumClasses: List<Class<*>>
    ): String {
        val enums = enumClasses
            .filter { it.isEnum }
            .sortedBy { it.simpleName }

        if (enums.isEmpty()) return ""

        val body = buildString {
            for (clazz in enums) {
                val name = clazz.simpleName ?: continue
                val values = clazz.enumConstants
                    ?.map { it.toString() }
                    .orEmpty()

                appendLine("export enum $name {")
                for (v in values) {
                    appendLine("""  $v = "$v",""")
                }
                appendLine("}")
                appendLine()
            }
        }.trimEnd()

        return """
// GENERATED FILE - DO NOT EDIT
// Source: Kotlin @Serializable DTOs (kxs-ts-gen)
// Date: $headerDate

$body

""".trimStart()
    }
}
