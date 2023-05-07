plugins {
    id("kotlin-jvm.base-conventions")
    id("kotlin-js.base-conventions")
}

dependencies {
    commonMainImplementation(project(":anvil-api"))
    commonMainImplementation(libs.ktor.serialization)
    jvmMainImplementation(libs.bundles.ktor.server)
    jvmMainImplementation(libs.ktor.client.cio)
    jsMainImplementation(libs.ktor.client.js)
}
