package nl.fbdevelopment.healthyplatform.di

import io.ktor.client.HttpClient
import nl.fbdevelopment.healthyplatform.api.createHttpClient
import nl.fbdevelopment.healthyplatform.api.service.AuthService
import nl.fbdevelopment.healthyplatform.api.service.AuthServiceImpl
import nl.fbdevelopment.healthyplatform.api.service.TimeLineService
import nl.fbdevelopment.healthyplatform.api.service.TimeLineServiceImpl
import nl.fbdevelopment.healthyplatform.api.service.UserService
import nl.fbdevelopment.healthyplatform.api.service.UserServiceImpl
import nl.fbdevelopment.healthyplatform.data.models.domain.user.User
import nl.fbdevelopment.healthyplatform.data.repository.AuthRepository
import nl.fbdevelopment.healthyplatform.data.repository.AuthRepositoryImpl
import nl.fbdevelopment.healthyplatform.data.repository.ClientAuthRepository
import nl.fbdevelopment.healthyplatform.data.repository.TimeLineRepository
import nl.fbdevelopment.healthyplatform.data.repository.TimeLineRepositoryImpl
import nl.fbdevelopment.healthyplatform.data.repository.UserRepository
import nl.fbdevelopment.healthyplatform.data.repository.UserRepositoryImpl
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

expect val platformModules: List<Module>

val commonServiceModule = module {
    single<HttpClient> { createHttpClient() }

    single<AuthService> { AuthServiceImpl(get()) }
    single<UserService> { UserServiceImpl(get()) }
    single<TimeLineService> { TimeLineServiceImpl(get()) }
}

val commonRepositoryModule = module {
    single<ClientAuthRepository> { AuthRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get())  }
    single<TimeLineRepository> { TimeLineRepositoryImpl(get()) }
}

fun commonModules(): List<Module> = listOf(
    commonServiceModule,
    commonRepositoryModule
)

fun initKoin() {
    startKoin {
        allowOverride(true)
        modules(commonModules() + platformModules)
    }
}




