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
//        allowHeaders { true }
        allowHeader(HttpHeaders.ContentType)

        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Get)

        exposeHeader(HttpHeaders.ContentDisposition)
        maxAgeInSeconds = 3600

        // Voor dev + tests: localhost toestaan (alle poorten)
        // Dit werkt voor:
        // - je React dev server (bijv. localhost:5173)
        // - je API zelf (localhost:8080)
        // - Ktor test client (localhost)
        allowHost("localhost:5174", schemes=listOf("http"))
        allowHost("localhost:5175", schemes=listOf("http"))
        allowHost("localhost:5176", schemes=listOf("http"))

        // Eventueel voor productie:
        // val frontendHost = System.getenv("FRONTEND_HOST") // bijv. "app.healthyproduct.nl"
        // if (!frontendHost.isNullOrBlank()) {
        //     allowHost(frontendHost, schemes = listOf("https"))
        // }
    }

    install(AutoHeadResponse)
    install(ConditionalHeaders)
}
