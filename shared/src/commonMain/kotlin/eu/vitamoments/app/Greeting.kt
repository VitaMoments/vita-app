package eu.vitamoments.app

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport


@Deprecated("This will be removed")
@OptIn(ExperimentalJsExport::class)
@JsExport
class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}