@file:OptIn(ExperimentalUuidApi::class)

package nl.fbdevelopment.healthyplatform.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import nl.fbdevelopment.healthyplatform.data.serializer.LocalDateTimeAsLongSerializer
import nl.fbdevelopment.healthyplatform.data.serializer.UuidSerializer
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

actual fun createHttpClient(): HttpClient = HttpClient(Js) {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = false
            ignoreUnknownKeys = true
            isLenient = true

            serializersModule = SerializersModule {
                contextual(Uuid::class, UuidSerializer)
                contextual(LocalDateTime::class, LocalDateTimeAsLongSerializer)
            }
        })
    }

    engine {}
}