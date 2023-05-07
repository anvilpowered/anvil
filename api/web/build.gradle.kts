plugins {
    id("kotlin-jvm.base-conventions")
}

dependencies {
    commonMainImplementation(project(":anvil-domain"))
    jvmMainImplementation(libs.bundles.ktor.client)
    jvmMainImplementation(libs.bundles.ktor.server)
}
