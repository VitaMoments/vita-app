package eu.vitamoments.app.config

import getEnv

object Config {
    private fun envName(): String =
        getEnv("ENVIRONMENT")?.trim()?.lowercase()
            ?: "dev"

    val currentEnvironment: AppEnvironment = when (BuildConfig.ENVIRONMENT.lowercase()) {
        "dev" -> AppEnvironment.Dev
        "test" -> AppEnvironment.Test
        "acc" -> AppEnvironment.Accept
        "demo" -> AppEnvironment.Demo
        "prod" -> AppEnvironment.Prod
        else -> AppEnvironment.Dev
    }

    val app: AppConfig
        get() = when (currentEnvironment) {
            AppEnvironment.Dev -> devConfig
            AppEnvironment.Test -> testConfig
            AppEnvironment.Accept -> accConfig
            AppEnvironment.Demo -> demoConfig
            AppEnvironment.Prod -> prodConfig
        }

    private val devConfig = AppConfig(
        appName = "VitaMoments DEV",
        apiBaseUrl = "https://api-dev.vitamoments.eu",
        dbName = "vita_development",
        theme = ThemeConfig(
            primaryColorHex = "#4CAF50",
            secondaryColorHex = "#8BC34A",
            backgroundColorHex = "#F5F5F5"
        ),
        enableDebugLogging = true,
    )

    private val testConfig = AppConfig(
        appName = "VitaMoments TEST",
        apiBaseUrl = "https://api-test.vitamoments.eu",
        dbName = "vita_test",
        theme = ThemeConfig(
            primaryColorHex = "#3F51B5",
            secondaryColorHex = "#2196F3",
            backgroundColorHex = "#F5F5F5"
        ),
        enableDebugLogging = true,
    )

    private val accConfig = AppConfig(
        appName = "VitaMoments ACC",
        apiBaseUrl = "https://api-acc.vitamoments.eu",
        dbName = "vita_accept",
        theme = ThemeConfig(
            primaryColorHex = "#FF9800",
            secondaryColorHex = "#FFC107",
            backgroundColorHex = "#FFFFFF"
        ),
        enableDebugLogging = false,
    )

    private val demoConfig = AppConfig(
        appName = "VitaMoments DEMO",
        apiBaseUrl = "https://api-demo.vitamoments.neu",
        dbName = "vita_demo",
        theme = ThemeConfig(
            primaryColorHex = "#9C27B0",
            secondaryColorHex = "#E91E63",
            backgroundColorHex = "#FFFFFF"
        ),
        enableDebugLogging = false,
    )

    private val prodConfig = AppConfig(
        appName = "VitaMoments",
        apiBaseUrl = "https://api.vitamoments.eu",
        dbName = "vita_prod",
        theme = ThemeConfig(
            primaryColorHex = "#00A86B",
            secondaryColorHex = "#007A4D",
            backgroundColorHex = "#FFFFFF"
        ),
        enableDebugLogging = false,
    )
}