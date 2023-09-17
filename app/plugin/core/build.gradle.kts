dependencies {
    api(project(":anvil-api"))
    api(platform(libs.adventure.bom))
    api("net.kyori:adventure-api") // TODO: -> compileOnlyApi ASAP
//    compileOnlyApi(libs.logging.api)
    api(libs.kbrig.core)
}
