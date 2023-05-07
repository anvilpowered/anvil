plugins {
    id("kotlin-jvm.base-conventions")
    id("kotlin-js.base-conventions")
}

dependencies {
    commonMainApi(libs.logging)
    commonMainApi(project(":anvil-domain"))
}
