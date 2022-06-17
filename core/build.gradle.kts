@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    api(project(":anvil-api"))
    api(libs.kotlin.reflect)
    api(libs.configurate.hocon)
    implementation(libs.reflections)
}
