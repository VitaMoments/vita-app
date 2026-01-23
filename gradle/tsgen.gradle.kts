import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.JavaExec

/**
 * Single TS generation entrypoint:
 * - generateTsModels : runs one JVM main that generates multiple .ts files
 * - syncTsModelsToWeb: copies generated files into webApp/src/generated
 *
 * Apply this script in :shared/build.gradle.kts
 */

// Generator-only configuration
val kxsTsGen = configurations.maybeCreate("kxsTsGen")

dependencies {
    // Maven Central
    kxsTsGen("dev.adamko.kxstsgen:kxs-ts-gen-core-jvm:0.2.4")
}

val generatedTsDir = layout.buildDirectory.dir("generated/ts")

val generateTsModels = tasks.register("generateTsModels", JavaExec::class.java) {
    group = "typescript"
    description = "Generate TypeScript models for DTO packages (feed/user/richtext/etc.)"

    dependsOn("compileKotlinJvm")
    notCompatibleWithConfigurationCache("Runs a JVM generator with dynamically resolved classpath")

    // Kotlin/JVM output locations
    val kotlinJvmClassesDir = layout.buildDirectory.dir("classes/kotlin/jvm/main")
    val jvmResourcesDirA = layout.buildDirectory.dir("processedResources/jvm/main")
    val jvmResourcesDirB = layout.buildDirectory.dir("resources/jvm/main") // fallback

    doFirst {
        val resolvedGenJars = kxsTsGen.resolve()

        logger.lifecycle("kxsTsGen resolved files:")
        resolvedGenJars.forEach { logger.lifecycle(" - $it") }

        classpath =
            files(
                kotlinJvmClassesDir.get().asFile,
                jvmResourcesDirA.get().asFile,
                jvmResourcesDirB.get().asFile
            ) +
                    configurations.getByName("jvmRuntimeClasspath") +
                    files(resolvedGenJars)
    }

    // One generator that writes multiple files into build/generated/ts
    mainClass.set("eu.vitamoments.app.tsgen.GenerateAllTsKt")

    // First arg = output dir
    args(generatedTsDir.get().asFile.absolutePath)
}

val syncTsModelsToWeb = tasks.register("syncTsModelsToWeb", Copy::class.java) {
    group = "typescript"
    description = "Copy generated TS models into webApp/src/generated"

    dependsOn(generateTsModels)

    from(generatedTsDir)
    include("**/*.ts")
    into(rootProject.layout.projectDirectory.dir("webApp/src/generated"))
}
