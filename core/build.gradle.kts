plugins {
    id("anvil-publish")
    id("anvil-sign")
    `java-library`
}

dependencies {
    api(libs.kbrig.brigadier)
    api(libs.kotlinx.serialization)
    api(libs.kotlinx.coroutines)
    api(libs.logging.api)
    api(libs.configurate.core)
    api(libs.configurate.hocon)
    api(libs.configurate.yaml)
    api(libs.configurate.extra.kotlin)
    api(libs.koin)

    compileOnly(libs.annotations)
    compileOnly(platform(libs.adventure.bom))
    compileOnly("net.kyori:adventure-api")
    compileOnly("net.kyori:adventure-text-minimessage")
}
