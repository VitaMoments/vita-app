package eu.vitamoments.app.modules

import io.ktor.server.application.Application
import eu.vitamoments.app.di.initKoinJvmSafe

fun Application.configureKoin() {
    initKoinJvmSafe()
}