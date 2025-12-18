package eu.vitamoments.app.di

import org.koin.dsl.module


val androidSpecificModule = module {

}

actual val platformModules = listOf(
    androidSpecificModule
)