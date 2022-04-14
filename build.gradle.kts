import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    kotlin("jvm")
}

allprojects {
    group = "org.anvilpowered"
    version = "0.4.0-SNAPSHOT"
    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://repo.spongepowered.org/repository/maven-public/")
        maven("https://packages.jetbrains.team/maven/p/xodus/xodus-daily")
        maven("https://papermc.io/repo/repository/maven-public/")
    }
    project.findProperty("buildNumber")
        ?.takeIf { version.toString().contains("SNAPSHOT") }
        ?.also { version = version.toString().replace("SNAPSHOT", "RC$it") }
    tasks {
        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = "17"
            kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
        }
        withType<JavaCompile> {
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

subprojects.forEach { project ->
    println("$project: ${project.sourceSets.main.get().java.sourceDirectories.joinToString(", ")}")
}
