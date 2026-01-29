package eu.vitamoments.app.modules

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.compression.deflate
import io.ktor.server.plugins.compression.gzip
import io.ktor.server.plugins.conditionalheaders.ConditionalHeaders
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.forwardedheaders.ForwardedHeaders
import io.ktor.server.plugins.forwardedheaders.XForwardedHeaders

fun Application.configureHTTP() {
    install(DefaultHeaders) {
        header("X-Frame-Options", "DENY")
        header("X-Content-Type-Options", "nosniff")
        header("X-Engine", "Ktor")
    }

    install(ForwardedHeaders)
    install(XForwardedHeaders)

    install(Compression) {
        gzip()
        deflate()
    }

    install(CORS) {
        allowCredentials = true

        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)

        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.Accept)

        exposeHeader(HttpHeaders.ContentDisposition)
        maxAgeInSeconds = 3600

        // DEV
        allowHost("localhost:5174", schemes = listOf("http"))
        allowHost("localhost:5175", schemes = listOf("http"))
        allowHost("localhost:5176", schemes = listOf("http"))

        // PROD (GitHub Pages origin)
        allowHost("vitamoments.github.io", schemes = listOf("https"))

        // Later custom domain (optioneel)
        val frontendHost = System.getenv("FRONTEND_HOST")
        if (!frontendHost.isNullOrBlank()) {
            allowHost(frontendHost, schemes = listOf("https"))
        }
    }

    install(AutoHeadResponse)
    install(ConditionalHeaders)
}
