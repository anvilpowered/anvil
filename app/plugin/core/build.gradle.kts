dependencies {
    api(project(":anvil-core"))
    api(platform(libs.adventure.bom))
    compileOnlyApi("net.kyori:adventure-api")
//    compileOnlyApi(libs.logging.api)
}
