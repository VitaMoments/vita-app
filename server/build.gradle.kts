plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application
}

group = "nl.fbdevelopment.healthyplatform"
version = "1.0.0"
application {
    mainClass.set("nl.fbdevelopment.healthyplatform.ApplicationKt")
    
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.logback)

    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.content.negotiation)

//    test
    testImplementation(libs.ktor.serverTestHost)
    testImplementation(enforcedPlatform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)

    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit5)

    // Core plugins
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    // HTTP / middleware
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.call.id)
    implementation(libs.ktor.server.compression)
    implementation(libs.ktor.server.default.headers)
    implementation(libs.ktor.server.auto.head.response)
    implementation(libs.ktor.server.forwarded.header)
    implementation(libs.ktor.server.conditional.headers)
    implementation(libs.ktor.server.caching.headers)
    implementation(libs.ktor.server.double.receive)
    implementation(libs.ktor.server.partial.content)
    implementation(libs.janino)


    // Optional / extras
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.server.resources)
    implementation(libs.ktor.server.rate.limit)

    implementation(libs.ktor.serialization.kotlinx.json)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.time)
    implementation(libs.exposed.json)

    implementation(libs.bcrypt)
    implementation(libs.dotenv.kotlin)

    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.default.headers)

    implementation(libs.koin.core)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger)

    implementation(libs.postgresql)

    // Image processing
    implementation(libs.thumbnailator)

}

tasks.test {
    useJUnitPlatform()
    environment("APP_ENV", "test")
}
