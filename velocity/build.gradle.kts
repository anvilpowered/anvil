plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.kapt")
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

repositories {
    maven("https://libraries.minecraft.net/")
    maven("https://repo.velocitypowered.com/snapshots/")
}

val velocityVersion: String by project

dependencies {
    implementation(project(":anvil-core"))
    compileOnly("com.velocitypowered:velocity-api:$velocityVersion")
    kapt("com.velocitypowered:velocity-api:$velocityVersion")
}
