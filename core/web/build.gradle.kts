plugins {
    id("kotlin-jvm.base-conventions")
    alias(libs.plugins.ktor)
    application
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

dependencies {
    commonMainImplementation(project(":anvil-core-domain"))
    jvmMainImplementation(libs.bundles.ktor.client)
    jvmMainImplementation(libs.bundles.ktor.server)
}
