@file:OptIn(ExperimentalUuidApi::class)

package eu.vitamoments.app.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import eu.vitamoments.app.data.serializer.LocalDateTimeAsLongSerializer
import eu.vitamoments.app.data.serializer.UuidSerializer
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

actual fun createHttpClient(): HttpClient = HttpClient(OkHttp) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
            serializersModule = SerializersModule {
                contextual(Uuid::class, UuidSerializer)
                contextual(LocalDateTime::class, LocalDateTimeAsLongSerializer)
            }
        })
    }
}