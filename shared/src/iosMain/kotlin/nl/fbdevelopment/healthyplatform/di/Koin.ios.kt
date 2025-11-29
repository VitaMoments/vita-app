package nl.fbdevelopment.healthyplatform.di

import org.koin.dsl.module

val iosSpecificModule = module {

}

actual val platformModules = listOf(
    iosSpecificModule
)