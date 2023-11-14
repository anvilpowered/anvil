plugins {
    id("anvil-publish")
    `java-library`
}

dependencies {
    api(platform(libs.exposed.bom))
    api(libs.bundles.exposed)
    api(libs.kbrig.brigadier)
    api(libs.logging.api)

    compileOnlyApi(platform(libs.adventure.bom))
    compileOnlyApi("net.kyori:adventure-api")
}
