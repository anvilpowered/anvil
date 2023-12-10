plugins {
    id("anvil-publish")
    id("anvil-sign")
}

dependencies {
    implementation(project(":anvil-core"))
    compileOnlyApi(libs.velocity)
}
