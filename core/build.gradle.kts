plugins {
    id("anvil-publish")
    `java-library`
}

dependencies {
    api(platform(libs.exposed.bom))
    api(libs.bundles.exposed)
    api(libs.kbrig.brigadier)
    api(libs.logging.api)
    api(libs.configurate.core)

    compileOnlyApi(platform(libs.adventure.bom))
    compileOnlyApi("net.kyori:adventure-api")
    compileOnlyApi("net.kyori:adventure-text-minimessage")
}
