plugins {
    id("kotlin-js.base-conventions")
    id("kotlin-jvm.base-conventions")
    id("anvil-publish")
}

dependencies {
    commonMainApi(libs.kotlinx.datetime)
    commonMainApi(libs.kontour)
    jvmMainApi(platform(libs.adventure.bom))
    jvmMainApi("net.kyori:adventure-api")
    jvmMainApi(libs.coroutines)
    jsMainImplementation(npm("uuid", "9.0.0"))
}
