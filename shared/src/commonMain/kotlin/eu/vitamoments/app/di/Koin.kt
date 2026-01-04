package eu.vitamoments.app.di

import io.ktor.client.HttpClient
import eu.vitamoments.app.api.createHttpClient
import eu.vitamoments.app.api.service.AuthService
import eu.vitamoments.app.api.service.AuthServiceImpl
import eu.vitamoments.app.api.service.FriendService
import eu.vitamoments.app.api.service.FriendServiceImpl
import eu.vitamoments.app.api.service.TimeLineService
import eu.vitamoments.app.api.service.TimeLineServiceImpl
import eu.vitamoments.app.api.service.UserService
import eu.vitamoments.app.api.service.UserServiceImpl
import eu.vitamoments.app.data.repository.AuthRepositoryImpl
import eu.vitamoments.app.data.repository.ClientAuthRepository
import eu.vitamoments.app.data.repository.FriendRepository
import eu.vitamoments.app.data.repository.FriendRepositoryImpl
import eu.vitamoments.app.data.repository.TimeLineRepository
import eu.vitamoments.app.data.repository.TimeLineRepositoryImpl
import eu.vitamoments.app.data.repository.UserRepository
import eu.vitamoments.app.data.repository.UserRepositoryImpl
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

expect val platformModules: List<Module>

val commonServiceModule = module {
    single<HttpClient> { createHttpClient() }

    single<AuthService> { AuthServiceImpl(get()) }
    single<UserService> { UserServiceImpl(get()) }
    single<TimeLineService> { TimeLineServiceImpl(get()) }
    single<FriendService> { FriendServiceImpl(get()) }
}

val commonRepositoryModule = module {
    single<ClientAuthRepository> { AuthRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get())  }
    single<TimeLineRepository> { TimeLineRepositoryImpl(get()) }
    single<FriendRepository> { FriendRepositoryImpl(get()) }
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




