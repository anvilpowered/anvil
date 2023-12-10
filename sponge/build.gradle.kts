plugins {
    id("anvil-publish")
    id("anvil-sign")
}

dependencies {
    api(project(":anvil-core"))
    compileOnlyApi(libs.sponge)
    implementation(libs.kotlinx.coroutines)
}
