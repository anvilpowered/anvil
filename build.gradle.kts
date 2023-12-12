import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.kotlin.serialization)
}

val projectVersion = file("version").readLines().first()

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "java-library")
    group = "org.anvilpowered"
    version = projectVersion
    System.getenv("BUILD_NUMBER")?.let { buildNumber ->
        version = when (val branch = System.getenv("VCS_BRANCH")?.replace('/', '-') ?: "unknown-branch") {
            "master" -> version.toString().replace("SNAPSHOT", "RC$buildNumber")
            else -> version.toString().replace("SNAPSHOT", "B$buildNumber-$branch")
        }
    }
    kotlin {
        compilerOptions {
            freeCompilerArgs = listOf(
                "-opt-in=kotlin.RequiresOptIn",
                "-Xcontext-receivers",
            )
        }
    }

    tasks {
        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = "17"
        }
        withType<JavaCompile> {
            options.encoding = "UTF-8"
            sourceCompatibility = "17"
            targetCompatibility = "17"
        }
    }
}
