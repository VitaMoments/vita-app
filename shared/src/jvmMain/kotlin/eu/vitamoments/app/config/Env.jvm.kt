package eu.vitamoments.app.config

actual fun getEnv(name: String): String? = System.getenv(name)