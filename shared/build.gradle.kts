import org.gradle.kotlin.dsl.implementation
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    iosArm64()
    iosSimulatorArm64()
    
    jvm()

    js(IR) {
        outputModuleName = "shared"
        binaries.library()
        generateTypeScriptDefinitions()
        compilerOptions {
            target = "es2015"
        }
        browser {
            commonWebpackConfig {
                cssSupport {} // ⚡ let op: dit is een **functie**, geen property
            }
        }
    }

    sourceSets {
        all {
            languageSettings.apply {
                optIn("kotlin.uuid.ExperimentalUuidApi")
                optIn("kotlin.time.ExperimentalTime")
            }
        }

        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.core)
            implementation(libs.kotlinx.serialization.json)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.server.content.negotiation)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.bcrypt)
            implementation(libs.koin.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(libs.exposed.core)
            implementation(libs.exposed.dao)
            implementation(libs.exposed.jdbc)
            implementation(libs.exposed.time)
            implementation(libs.exposed.json)

            implementation(libs.ktor.server.auth)
            implementation(libs.ktor.server.auth.jwt)


            implementation(libs.ktor.client.cio)

            implementation(libs.bcrypt)
            implementation(libs.dotenv.kotlin)

            implementation(libs.koin.core.jvm)

        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.koin.android)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        jsMain.dependencies {
            implementation(libs.ktor.client.js)
            implementation(libs.koin.core.js)
        }
    }
}

android {
    namespace = "eu.vitamoments.app.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

abstract class GenerateBuildConfig : DefaultTask() {

    @get:Input
    abstract val environment: Property<String>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val pkg = "eu.vitamoments.app.config"
        val envValue = environment.get() // dev / test / acc / demo / prod

        val file = outputDir.get().file("BuildConfig.kt").asFile
        file.parentFile.mkdirs()
        file.writeText(
            """
            package $pkg

            object BuildConfig {
                const val ENVIRONMENT: String = "$envValue"
            }
            """.trimIndent()
        )
    }
}

// Default = "dev", maar je kunt override met -PappEnvironment=test etc.
// Eerst naar env var APP_ENV kijken, anders naar gradle property, anders "dev"
val appEnvironment = providers
    .environmentVariable("APP_ENV")
    .orElse(providers.gradleProperty("appEnvironment").orElse("dev"))

val generateBuildConfig = tasks.register<GenerateBuildConfig>("generateBuildConfig") {
    environment.set(appEnvironment)
    outputDir.set(layout.buildDirectory.dir("generated/buildConfig/commonMain/kotlin"))
}

kotlin.sourceSets.named("commonMain") {
    kotlin.srcDir(layout.buildDirectory.dir("generated/buildConfig/commonMain/kotlin"))
}

tasks.withType<KotlinCompile>().configureEach {
    if (name.contains("commonMain", ignoreCase = true)) {
        dependsOn(generateBuildConfig)
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>()
    .matching { it.name.startsWith("compileKotlinJs") || it.name.contains("Js") }
    .configureEach {
        dependsOn(generateBuildConfig)
    }