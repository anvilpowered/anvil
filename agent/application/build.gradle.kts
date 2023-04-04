plugins {
    id("kotlin-jvm.base-conventions")
}

dependencies {
    commonMainApi(project(":anvil-core-domain"))
    jvmMainImplementation(libs.brigadier)
    jvmMainImplementation(platform(libs.adventure.bom))
    jvmMainImplementation("net.kyori:adventure-api")
    jvmMainImplementation(libs.logging.api)
}
