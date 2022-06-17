@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.jvm)
    id("org.jetbrains.kotlin.kapt")
    alias(libs.plugins.shadow)
}

val velocityVersion: String by project

dependencies {
    implementation(project(":anvil-core"))
    compileOnly(libs.velocity)
    kapt(libs.velocity)
}
