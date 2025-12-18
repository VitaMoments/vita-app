package eu.vitamoments.app.di

import org.koin.dsl.module

val iosSpecificModule = module {

}

actual val platformModules = listOf(
    iosSpecificModule
)