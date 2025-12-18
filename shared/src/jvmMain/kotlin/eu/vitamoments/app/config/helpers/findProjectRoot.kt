package eu.vitamoments.app.config.helpers

import java.io.File

tailrec fun findProjectRoot(startDir: File = File(System.getProperty("user.dir"))): File {
    val envFile = File(startDir, ".env")
    return when {
        envFile.exists() -> startDir
        startDir.parentFile == null -> startDir
        else -> findProjectRoot(startDir.parentFile)
    }
}