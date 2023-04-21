plugins {
    id("kotlin-jvm.base-conventions")
    id("anvil-publish")
    alias(libs.plugins.shadow)
    `java-library`
}

dependencies {
    commonMainApi(project(":anvil-core-domain"))
    jvmMainApi(platform(libs.adventure.bom))
    jvmMainApi("net.kyori:adventure-api") // TODO: -> compileOnly ASAP
//    compileOnlyApi(libs.logging.api)
    commonMainApi(libs.kbrig.core)
}
