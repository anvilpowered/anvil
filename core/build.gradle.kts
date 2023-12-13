plugins {
    id("anvil-publish")
    id("anvil-sign")
    `java-library`
}

dependencies {
    api(platform(libs.exposed.bom))
    api(libs.kotlinx.serialization)
    api(libs.kotlinx.coroutines)
    api(libs.bundles.exposed)
    api(libs.kbrig.brigadier)
    api(libs.logging.api)
    api(libs.configurate.core)
    api(libs.configurate.hocon)
    api(libs.configurate.yaml)
    api(libs.annotations)
    api(libs.guava)
    api(libs.koin)

    compileOnlyApi(platform(libs.adventure.bom))
    compileOnlyApi("net.kyori:adventure-api")
    compileOnlyApi("net.kyori:adventure-text-minimessage")

    runtimeElements(libs.postgresql)
}
