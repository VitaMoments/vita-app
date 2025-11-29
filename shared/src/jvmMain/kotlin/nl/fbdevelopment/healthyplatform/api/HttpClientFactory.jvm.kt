package nl.fbdevelopment.healthyplatform.api

import io.ktor.client.HttpClient

actual fun createHttpClient(): HttpClient {
    error("createHttpClient() should not be used on JVM. Use serverNetworkModule instead.")
}