package nl.fbdevelopment.healthyplatform.config

data class AppConfig(
    val appName: String,
    val apiBaseUrl: String,
    val dbName: String,
    val theme: ThemeConfig,
    val enableDebugLogging: Boolean,
)
