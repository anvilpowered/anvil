plugins {
    id("anvil-publish")
}

dependencies {
    implementation(libs.kotlinx.coroutines)
    implementation(project(":anvil-core"))
    implementation(libs.sponge)
}
