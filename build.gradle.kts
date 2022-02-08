import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    kotlin("jvm")
}

//ext.apiVersion = "0.4-SNAPSHOT"
subprojects {
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
            kotlinOptions.jvmTarget = "11"
            kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
        }
    }
}
