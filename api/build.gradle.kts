plugins {
    id("kotlin-jvm.base-conventions")
    id("kotlin-js.base-conventions")
    id("anvil-publish")
}

dependencies {
    commonMainApi(libs.logging)
    commonMainApi(project(":anvil-domain"))
}
