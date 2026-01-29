package eu.vitamoments.app

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import eu.vitamoments.app.modules.configureDatabase
import eu.vitamoments.app.modules.configureHTTP
import eu.vitamoments.app.modules.configureKoin
import eu.vitamoments.app.modules.configureMonitoring
import eu.vitamoments.app.modules.configureRouting
import eu.vitamoments.app.modules.security.configureSecurity
import eu.vitamoments.app.modules.configureSerialization

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080
    embeddedServer(Netty, port = port, module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureKoin()
    configureDatabase()
    configureSerialization()
    configureMonitoring()
    configureHTTP()
    configureSecurity()
    configureRouting()
}

fun Application.testModule() {
    configureKoin()
    configureDatabase()
    configureSerialization()
    configureHTTP()
    configureSecurity()
    configureRouting()
}