plugins {
    id("kotlin-jvm.base-conventions")
    id("anvil-publish")
    alias(libs.plugins.shadow)
    `java-library`
}

dependencies {
    compileOnlyApi(platform(libs.adventure.bom))
    commonMainApi(project(":anvil-core-domain"))
    commonMainApi("net.kyori:adventure-api") // TODO: -> compileOnly ASAP
//    compileOnlyApi(libs.logging.api)
    commonMainApi(libs.kbrig.core)
}
