plugins {
    id("anvil-publish")
    `java-library`
}

dependencies {
    api(project(":anvil-api"))

    api(platform(libs.exposed.bom))
    api(libs.bundles.exposed)

    compileOnlyApi(platform(libs.adventure.bom))
    compileOnlyApi("net.kyori:adventure-api")

    api(libs.kontour)
}
