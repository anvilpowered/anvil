plugins {
    id("kotlin-jvm.base-conventions")
    id("anvil-publish")
}

dependencies {
    commonMainApi(project(":anvil-core-domain"))
    jvmMainApi(libs.brigadier)
    jvmMainApi(platform(libs.adventure.bom))
    jvmMainApi("net.kyori:adventure-api")
    jvmMainApi(libs.logging.api)
}
