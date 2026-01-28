package eu.vitamoments.app.api

import eu.vitamoments.app.data.serializer.InstantSerializer
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlin.time.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import eu.vitamoments.app.data.serializer.LocalDateTimeAsLongSerializer
import eu.vitamoments.app.data.serializer.UuidSerializer
import kotlin.uuid.Uuid

actual fun createHttpClient(): HttpClient = HttpClient(Darwin) {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = false
            ignoreUnknownKeys = true
            isLenient = true

            serializersModule = SerializersModule {
                contextual(Uuid::class, UuidSerializer)
                contextual(LocalDateTime::class, LocalDateTimeAsLongSerializer)
                contextual(Instant::class, InstantSerializer)
            }
        })
    }

    engine {
        configureRequest {
            setAllowsCellularAccess(true)
        }
    }
}