import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
}

val projectVersion = file("version").readLines().first()
project.extra["apiVersion"] = projectVersion.replace(".[0-9]+(?=($|-SNAPSHOT))".toRegex(), "")

allprojects {
    group = "org.anvilpowered"
    version = projectVersion
    project.findProperty("buildNumber")
        ?.takeIf { version.toString().contains("SNAPSHOT") }
        ?.also { version = version.toString().replace("SNAPSHOT", "RC$it") }
    tasks {
        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = "17"
            kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
        }
        withType<JavaCompile> {
            options.encoding = "UTF-8"
            targetCompatibility = "17"
            sourceCompatibility = "17"
        }
    }
}

// include legacy java code during migration to kotlin
subprojects {
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    val legacyName = when (this@subprojects.name) {
        "anvil-core" -> "anvil-common"
        "anvil-paper" -> "anvil-spigot"
        else -> this@subprojects.name
    }
    java {
        sourceSets.main.configure {
            java.srcDir("../$legacyName/src/main/java")
        }
    }
    kotlin {
        sourceSets.main.configure {
            kotlin.srcDir("../$legacyName/src/main/kotlin")
        }
    }
}
