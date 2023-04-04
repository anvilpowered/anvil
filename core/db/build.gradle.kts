plugins {
    id("kotlin-jvm.base-conventions")
}

dependencies {
    commonMainApi(project(":anvil-core-domain"))
    jvmMainApi(platform(libs.exposed.bom))
    jvmMainApi(libs.bundles.exposed)
}
