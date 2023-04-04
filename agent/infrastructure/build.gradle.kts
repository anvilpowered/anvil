plugins {
    id("kotlin-jvm.base-conventions")
}

dependencies {
    commonMainImplementation(libs.bundles.ktor.client)
    commonMainImplementation(libs.bundles.ktor.server)
}
