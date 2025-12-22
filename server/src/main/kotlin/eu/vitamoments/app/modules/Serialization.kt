@file:OptIn(ExperimentalUuidApi::class)

package eu.vitamoments.app.modules

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.respond
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import eu.vitamoments.app.data.serializer.LocalDateTimeAsLongSerializer
import eu.vitamoments.app.data.serializer.UuidSerializer
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(
            Json {
                classDiscriminator = "type";
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true

                serializersModule = SerializersModule {
                    contextual(Uuid::class, UuidSerializer)
                    contextual(LocalDateTime::class, LocalDateTimeAsLongSerializer)
                }
            }
        )
    }

    install(StatusPages) {
        exception<kotlinx.serialization.SerializationException> { call, cause ->
            call.application.environment.log.warn("❌ JSON parse: ${cause.message}")
            call.respond(HttpStatusCode.BadRequest, "Invalid JSON")
        }
        exception<IllegalArgumentException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, cause.message ?: "Bad request")
        }
    }

}