package nl.fbdevelopment.healthyplatform

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import nl.fbdevelopment.healthyplatform.modules.configureDatabase
import nl.fbdevelopment.healthyplatform.modules.configureHTTP
import nl.fbdevelopment.healthyplatform.modules.configureKoin
import nl.fbdevelopment.healthyplatform.modules.configureMonitoring
import nl.fbdevelopment.healthyplatform.modules.configureRouting
import nl.fbdevelopment.healthyplatform.modules.security.configureSecurity
import nl.fbdevelopment.healthyplatform.modules.configureSerialization

fun main() {
    val port = System.getenv("SERVER_PORT")?.toInt() ?: 8080
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