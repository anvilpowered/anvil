plugins {
    id("anvil-publish")
    id("anvil-sign")
    `java-library`
}

dependencies {
    api(platform(libs.exposed.bom))
    api(libs.bundles.exposed)
    api(libs.kbrig.brigadier)
    api(libs.logging.api)
    api(libs.configurate.core)
    api(libs.annotations)
    api(libs.kotlinx.coroutines)
    api(libs.guava)
    api(libs.koin)

    compileOnlyApi(platform(libs.adventure.bom))
    compileOnlyApi("net.kyori:adventure-api")
    compileOnlyApi("net.kyori:adventure-text-minimessage")
}
