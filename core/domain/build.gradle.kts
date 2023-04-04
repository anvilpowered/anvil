plugins {
    id("kotlin-js.base-conventions")
    id("kotlin-jvm.base-conventions")
}

dependencies {
    commonMainApi(libs.kotlinx.datetime)
    jvmMainApi(platform(libs.adventure.bom))
    jvmMainApi("net.kyori:adventure-api")
    jvmMainApi(libs.coroutines)
    jsMainImplementation(npm("uuid", "9.0.0"))
}
