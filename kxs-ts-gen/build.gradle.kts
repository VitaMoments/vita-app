plugins {
    // Gebruik Kotlin JVM (processor draait op de JVM)
    alias(libs.plugins.kotlinJvm)
}

kotlin {
    // Processor compileert op JDK 17 (mag ook 11, maar 17 is tegenwoordig veilig/stabiel)
    jvmToolchain(17)
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(libs.ksp.api)
}
