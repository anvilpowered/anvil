plugins {
    id("kotlin-jvm.base-conventions")
}

dependencies {
    commonMainApi(project(":anvil-api-game"))
    jvmMainApi(platform(libs.adventure.bom))
    jvmMainApi("net.kyori:adventure-api") // TODO: -> compileOnlyApi ASAP
//    compileOnlyApi(libs.logging.api)
    commonMainApi(libs.kbrig.core)
}
