import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.ByteArrayOutputStream

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.kotlin.serialization)
}

val projectVersion: String = run {
    val rawVersion = file("version").readLines().first()
    if (project.hasProperty("rawVersion")) {
        rawVersion
    } else {
        val branch = System.getenv("VCS_BRANCH")?.replace('/', '-') ?: "unknown-branch"
        System.getenv("BUILD_NUMBER")?.let { buildNumber ->
            val gitRev = ByteArrayOutputStream()
            exec {
                commandLine("git", "rev-parse", "--short", "HEAD")
                standardOutput = gitRev
            }.assertNormalExitValue()
            rawVersion.replace("SNAPSHOT", "BETA$buildNumber-$branch-${gitRev.toString().trim()}")
        } ?: rawVersion
    }
}

logger.warn("Resolved project version $projectVersion")

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "java-library")

    group = "org.anvilpowered"
    version = projectVersion

    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_21
            freeCompilerArgs = listOf(
                "-opt-in=kotlin.RequiresOptIn",
                "-Xcontext-receivers",
            )
        }
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }
}
