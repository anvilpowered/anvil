plugins {
    id("anvil-publish")
}

dependencies {
    implementation(project(":anvil-core"))
    compileOnlyApi(libs.velocity)
}
