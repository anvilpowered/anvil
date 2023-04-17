plugins {
    id("kotlin-js.base-conventions")
    id("kotlin-jvm.base-conventions")
    id("anvil-publish")
    `java-library`
}

dependencies {
    commonMainApi(libs.kotlinx.datetime)
    commonMainApi(libs.kontour) {
        exclude("org.slf4j")
    }
    commonMainApi(libs.coroutines)
    compileOnlyApi(platform(libs.adventure.bom))
    compileOnlyApi("net.kyori:adventure-api")
    jsMainImplementation(npm("uuid", "9.0.0"))
}
