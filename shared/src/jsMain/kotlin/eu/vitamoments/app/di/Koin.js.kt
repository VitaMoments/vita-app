package eu.vitamoments.app.di

import org.koin.dsl.module

val jsSpecificModule = module {

}

actual val platformModules = listOf(
    jsSpecificModule
)