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
import eu.vitamoments.app.data.media.LocalMediaStorage
import eu.vitamoments.app.data.media.MediaAccessService
import eu.vitamoments.app.data.media.MediaService
import eu.vitamoments.app.data.media.MediaStorage
import eu.vitamoments.app.data.media.MediaValidationService
import eu.vitamoments.app.data.repository.BlogRepository
import eu.vitamoments.app.data.repository.FriendRepository
import eu.vitamoments.app.data.repository.JVMAuthRepository
import eu.vitamoments.app.data.repository.JVMBlogRepository
import eu.vitamoments.app.data.repository.JVMFriendRepository
import eu.vitamoments.app.data.repository.JVMMediaRepository
import eu.vitamoments.app.data.repository.JVMTimeLineRepository
import eu.vitamoments.app.data.repository.JVMUserRepository
import eu.vitamoments.app.data.repository.MediaRepository
import eu.vitamoments.app.data.repository.ServerAuthRepository
import eu.vitamoments.app.data.repository.TimeLineRepository
import eu.vitamoments.app.data.repository.UserRepository
import eu.vitamoments.app.data.serializer.InstantSerializer
import eu.vitamoments.app.data.serializer.LocalDateTimeAsLongSerializer
import eu.vitamoments.app.data.serializer.UuidSerializer
import org.koin.core.context.GlobalContext
import org.koin.dsl.module
import kotlin.time.Instant
import kotlin.uuid.Uuid

private val jvmRepositoryModule = module {
    single<ServerAuthRepository> { JVMAuthRepository() }
    single<UserRepository> { JVMUserRepository(mediaRepository = get()) }
    single<TimeLineRepository> { JVMTimeLineRepository()  }
    single<FriendRepository> { JVMFriendRepository() }
    single<BlogRepository> { JVMBlogRepository() }
    single<MediaRepository> { JVMMediaRepository() }
}

private val jvmStorageModule = module {
    single<MediaStorage> {
        LocalMediaStorage(
            baseDir = "./media"
        )
    }
}

private val jvmServiceModule = module {
    single<MediaValidationService> { MediaValidationService() }
    single<MediaAccessService> { MediaAccessService() }
    single<MediaService> {
        MediaService(
            mediaRepository = get(),
            mediaStorage = get(),
            mediaAccessService = get(),
            mediaValidationService = get()
        )
    }
}

private val networkModule = module {
    single {
        val json = Json {
            classDiscriminator = "type"
            ignoreUnknownKeys = true
            isLenient = true
            prettyPrint = false
            explicitNulls = false

            serializersModule = SerializersModule {
                contextual(Uuid::class, UuidSerializer)
                contextual(LocalDateTime::class, LocalDateTimeAsLongSerializer)
                contextual(Instant::class, InstantSerializer)
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
    networkModule,
    jvmStorageModule,
    jvmServiceModule
)

fun initKoinJvmSafe() {
    if (GlobalContext.getOrNull() == null) {
        initKoin()
    }
}