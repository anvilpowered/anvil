@file:Suppress("UnstableApiUsage")

plugins {
    id("kotlin-jvm.base-conventions")
    alias(libs.plugins.shadow)
}

dependencies {
    commonMainImplementation(project(":anvil-app-plugin-paper"))
    commonMainImplementation(project(":anvil-app-plugin-sponge"))
    commonMainImplementation(project(":anvil-app-plugin-velocity"))
}

tasks {
    shadowJar {
        archiveFileName = "anvil-agent-${project.version}.jar"
    }
}
