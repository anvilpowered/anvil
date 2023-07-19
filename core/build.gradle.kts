plugins {
    id("kotlin-jvm.base-conventions")
    id("kotlin-js.base-conventions")
    id("anvil-publish")
}

dependencies {
    commonMainApi(project(":anvil-api"))
    jvmMainApi(platform(libs.exposed.bom))
    jvmMainApi(libs.bundles.exposed)
}
