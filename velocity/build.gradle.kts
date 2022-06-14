@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.jvm)
    id("org.jetbrains.kotlin.kapt")
    alias(libs.plugins.shadow)
}

repositories {
    maven("https://libraries.minecraft.net/")
    maven("https://repo.papermc.io/repository/maven-public/")
}

val velocityVersion: String by project

dependencies {
    implementation(project(":anvil-core"))
    compileOnly(libs.velocity)
    kapt(libs.velocity)
}
