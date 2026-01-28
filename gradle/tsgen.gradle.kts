import org.gradle.api.tasks.Delete

// Vereist: in de module (shared) plugins block:
// alias(libs.plugins.ksp)

if (!plugins.hasPlugin("com.google.devtools.ksp")) {
    throw GradleException(
        "KSP plugin is not applied. Add `alias(libs.plugins.ksp)` to the shared module plugins block."
    )
}

dependencies {
    add("kspCommonMainMetadata", project(":kxs-ts-gen"))
}

// Deterministisch pad (CC-safe). Zet dit in gradle.properties voor jouw repo:
// tsgenOutDir=../webApp/src/data/types   (of wat jij wil)
val outDirPath = providers.gradleProperty("tsgenOutDir")
    .orElse("webApp/src/data/types")
    .get()

val resolvedOutDir = rootProject.file(outDirPath)

// Configure KSP args zonder KspExtension import (werkt in applied scripts)
val kspExt = extensions.getByName("ksp")
val argMethod = kspExt.javaClass.methods.firstOrNull { m ->
    m.name == "arg" &&
            m.parameterTypes.size == 2 &&
            m.parameterTypes[0] == String::class.java &&
            m.parameterTypes[1] == String::class.java
} ?: error("Could not find method ksp.arg(String, String).")

argMethod.invoke(kspExt, "tsgen.basePackage", "eu.vitamoments.app.data.models")
argMethod.invoke(kspExt, "tsgen.discriminatorKey", "type")
argMethod.invoke(kspExt, "tsgen.outDir", resolvedOutDir.absolutePath)

// Zoek KSP metadata tasks voor commonMain (tasknamen verschillen per KSP/Kotlin versie)
val kspMetadataTasks = tasks.matching { t ->
    t.name.startsWith("ksp", ignoreCase = true) &&
            t.name.contains("CommonMain", ignoreCase = true) &&
            t.name.contains("Metadata", ignoreCase = true)
}

// Fallback als hij anders heet (bijv. geen "CommonMain" in de naam)
val kspFallbackTasks = tasks.matching { t ->
    t.name.startsWith("ksp", ignoreCase = true) &&
            t.name.contains("Metadata", ignoreCase = true)
}

val chosenKspTasks = if (kspMetadataTasks.isEmpty()) kspFallbackTasks else kspMetadataTasks

// Clean task blijft hetzelfde (Delete)
val cleanTsTypes = tasks.register<Delete>("cleanTsTypes") {
    delete(resolvedOutDir)
}

// Configureer alle gevonden KSP metadata tasks
chosenKspTasks.configureEach {
    outputs.upToDateWhen { false }
    outputs.cacheIf { false }
    dependsOn(cleanTsTypes)
}

// Alias task
tasks.register("generateTsTypes") {
    group = "codegen"
    description = "Generate TypeScript types into the configured tsgenOutDir from Kotlin @Serializable models"

    if (chosenKspTasks.isEmpty()) {
        throw GradleException(
            "No KSP metadata task found. Make sure KSP is applied and commonMain is configured. " +
                    "Run `./gradlew :shared:tasks --all | grep ksp` to see available tasks."
        )
    }

    dependsOn(chosenKspTasks)
}
