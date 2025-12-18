package eu.vitamoments.app.modules

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.*
import io.ktor.server.plugins.callid.CallId
import io.ktor.server.plugins.callid.callId
import org.slf4j.event.Level
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.doublereceive.DoubleReceive
import io.ktor.server.request.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun Application.configureMonitoring() {
    install(CallLogging) {
        mdc("callId") { call -> call.callId }
        level = Level.INFO
        filter { call ->
            call.request.path().startsWith("/")
        }
        format { call ->
            val status = call.response.status()?.value ?: "-"
            val method = call.request.httpMethod.value
            val uri = call.request.uri
            val id = call.callId ?: "-"
            "[$status] $method $uri (callId=$id)"
        }
    }

    // 3. DoubleReceive zodat we de body kunnen lezen én routes nog steeds kunnen receive()-en
    install(DoubleReceive)

    // 4. Custom interceptor: headers + body loggen
    intercept(ApplicationCallPipeline.Plugins) {
        val request = call.request
        val method = request.httpMethod
        val uri = request.uri
        val callId = call.callId ?: "-"

        // Headers als string
        val headersString = request.headers.entries()
            .joinToString(separator = "\n") { (name, values) ->
                "$name: ${values.joinToString()}"
            }

        // Alleen body loggen voor methods die normaal een body hebben
        val hasBody = method in listOf(HttpMethod.Post, HttpMethod.Put, HttpMethod.Patch)
        val bodyText = if (hasBody) {
            try {
                // Dankzij DoubleReceive breken we hiermee de normale call.receive() niet
                call.receiveText()
            } catch (e: Exception) {
                "<failed to read body: ${e.message}>"
            }
        } else {
            ""
        }

        application.log.info(
            buildString {
                appendLine(">>> [${callId}] ${method.value} $uri")
                appendLine("Headers:")
                appendLine(if (headersString.isBlank()) "<no headers>" else headersString)
                if (hasBody) {
                    appendLine("Body:")
                    appendLine(if (bodyText.isBlank()) "<empty body>" else bodyText)
                }
                appendLine("<<< end request log")
            }
        )


        // heel belangrijk: ga verder in de pipeline
        proceed()
    }

    install(CallId) {
        header(HttpHeaders.XRequestId)
        generate { Uuid.random().toString() }
        verify { it.isNotEmpty() }
        reply { call, callId ->
            call.response.headers.append(HttpHeaders.XRequestId, callId)
        }
    }
    environment.log.info("✅ Monitoring configured (CallLogging + CallId)")
}
