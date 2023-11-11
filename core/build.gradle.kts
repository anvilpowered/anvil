plugins {
    id("anvil-publish")
    `java-library`
}

dependencies {
    api(platform(libs.exposed.bom))
    api(libs.bundles.exposed)
    api(libs.kbrig.brigadier)

    compileOnlyApi(platform(libs.adventure.bom))
    compileOnlyApi("net.kyori:adventure-api")
}
