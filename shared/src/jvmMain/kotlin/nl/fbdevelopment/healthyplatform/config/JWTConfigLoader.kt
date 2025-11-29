package nl.fbdevelopment.healthyplatform.config

import io.github.cdimascio.dotenv.dotenv
import nl.fbdevelopment.healthyplatform.config.helpers.findProjectRoot

object JWTConfigLoader {
    private val dotenv by lazy {
        runCatching {
            val rootDir = findProjectRoot()
            println("Shared EnvConfig: loading .env from: ${rootDir.absolutePath}")

            dotenv {
                directory = rootDir.absolutePath
                filename = ".env"
                ignoreIfMissing = true
            }
        }.getOrNull()
    }

    private fun read(key: String, default: String? = null): String? {
        val env = System.getenv(key)
        if (!env.isNullOrBlank()) return env

        val prop = System.getProperty(key)
        if (!prop.isNullOrBlank()) return prop

        val envFile = dotenv?.get(key)
        if (!envFile.isNullOrBlank()) return envFile

        return default
    }

    fun loadOrThrow(): JWTConfig {
        val issuer  = read("JWT_ISSUER") ?: error("JWT_ISSUER is vereist")
        val audience= read("JWT_AUDIENCE") ?: error("JWT_AUDIENCE is vereist")
        val secret  = read("JWT_SECRET") ?: error("JWT_SECRET is vereist (niet bundelen; zet als env var)")
        val realm   = read("JWT_REALM", "access")!!
        val exp     = read("JWT_EXP_SECONDS", "3600")!!.toLong()
        val refExp  = read("JWT_REFRESH_EXP_SECONDS", "604800")!!.toLong()

        return JWTConfig(
            issuer = issuer,
            audience = audience,
            realm = realm,
            secret = secret,
            jwtExpirationSeconds = exp,
            refreshExpirationSeconds = refExp
        )
    }
}