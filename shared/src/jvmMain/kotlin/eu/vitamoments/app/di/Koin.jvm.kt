@file:OptIn(ExperimentalUuidApi::class)

package eu.vitamoments.app.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import eu.vitamoments.app.config.JWTConfigLoader
import eu.vitamoments.app.data.repository.JVMAuthRepository
import eu.vitamoments.app.data.repository.JVMTimeLineRepository
import eu.vitamoments.app.data.repository.JVMUserRepository
import eu.vitamoments.app.data.repository.ServerAuthRepository
import eu.vitamoments.app.data.repository.TimeLineRepository
import eu.vitamoments.app.data.repository.UserRepository
import eu.vitamoments.app.data.serializer.LocalDateTimeAsLongSerializer
import eu.vitamoments.app.data.serializer.UuidSerializer
import org.koin.core.context.GlobalContext
import org.koin.dsl.module
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private val jvmRepositoryModule = module {
    single<ServerAuthRepository> { JVMAuthRepository() }
    single<UserRepository> { JVMUserRepository() }
    single<TimeLineRepository> { JVMTimeLineRepository()  }
}

private val networkModule = module {
    single {
        val json = Json {
            classDiscriminator = "type";
            ignoreUnknownKeys = true
            isLenient = true
            prettyPrint = false

            serializersModule = SerializersModule {
                contextual(Uuid::class, UuidSerializer)
                contextual(LocalDateTime::class, LocalDateTimeAsLongSerializer)
            }
        }

        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(json)
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 15_000
                connectTimeoutMillis = 10_000
                socketTimeoutMillis = 15_000
            }
        }
    }

    single { JWTConfigLoader.loadOrThrow() }
}

actual val platformModules = listOf(
    jvmRepositoryModule,
    networkModule
)

fun initKoinJvmSafe() {
    if (GlobalContext.getOrNull() == null) {
        initKoin()
    }
}