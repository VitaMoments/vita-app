package eu.vitamoments.app.utils

import io.ktor.server.testing.ApplicationTestBuilder
import eu.vitamoments.app.di.initKoinJvmSafe
import eu.vitamoments.app.testModule

/**
 * Korte helper om je Ktor app te bootstrappen in tests.
 */
fun ApplicationTestBuilder.setupApp() {
    application {
        initKoinJvmSafe()
        testModule()
    }
}