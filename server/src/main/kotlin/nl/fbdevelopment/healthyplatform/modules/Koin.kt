package nl.fbdevelopment.healthyplatform.modules

import io.ktor.server.application.Application
import nl.fbdevelopment.healthyplatform.di.initKoinJvmSafe

fun Application.configureKoin() {
    initKoinJvmSafe()
}