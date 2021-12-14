plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.kapt")
}

repositories {
    maven("https://libraries.minecraft.net/")
    maven("https://repo.velocitypowered.com/snapshots/")
}

val velocityVersion: String by project

dependencies {
    implementation(project(":anvil-core"))
    implementation("com.velocitypowered:velocity-api:$velocityVersion")
    kapt("com.velocitypowered:velocity-api:$velocityVersion")
}
