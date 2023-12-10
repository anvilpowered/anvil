plugins {
    id("anvil-publish")
    id("anvil-sign")
}

dependencies {
    implementation(libs.kotlinx.coroutines)
    implementation(project(":anvil-core"))
    implementation(libs.sponge)
}
