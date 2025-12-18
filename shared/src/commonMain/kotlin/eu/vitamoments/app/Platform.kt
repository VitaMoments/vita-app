package eu.vitamoments.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform