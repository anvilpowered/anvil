plugins {
    id("kotlin-js.base-conventions")
    id("kotlin-jvm.base-conventions")
    id("anvil-publish")
    `java-library`
}

dependencies {
    commonMainApi(libs.annotations)
    commonMainApi(libs.kbrig.core)
    commonMainApi(libs.kotlinx.datetime)
    commonMainApi(libs.kontour) {
        exclude("org.slf4j")
    }
    commonMainApi(libs.kotlinx.coroutines)
    jvmMainApi(platform(libs.adventure.bom))
    jvmMainApi("net.kyori:adventure-api")
    jvmMainApi("net.kyori:adventure-text-minimessage")
    jsMainImplementation(npm("uuid", "9.0.0"))
}
