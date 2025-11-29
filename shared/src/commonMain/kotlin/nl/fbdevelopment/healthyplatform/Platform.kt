package nl.fbdevelopment.healthyplatform

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform