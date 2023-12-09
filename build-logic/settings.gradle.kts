@file:Suppress("UnstableApiUsage")

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    versionCatalogs {
        register("libs") {
            from(files("../gradle/libs.versions.toml")) // include from parent project
        }
    }
}

rootProject.name = "build-logic"
