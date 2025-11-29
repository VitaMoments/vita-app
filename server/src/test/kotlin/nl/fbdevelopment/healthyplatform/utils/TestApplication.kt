package nl.fbdevelopment.healthyplatform.utils

import io.ktor.server.testing.ApplicationTestBuilder
import nl.fbdevelopment.healthyplatform.di.initKoinJvmSafe
import nl.fbdevelopment.healthyplatform.testModule

/**
 * Korte helper om je Ktor app te bootstrappen in tests.
 */
fun ApplicationTestBuilder.setupApp() {
    application {
        initKoinJvmSafe()
        testModule()
    }
}