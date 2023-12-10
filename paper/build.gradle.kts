plugins {
    id("anvil-publish")
    id("anvil-sign")
}

dependencies {
    implementation(project(":anvil-core"))
    implementation(libs.kbrig.brigadier)
    compileOnly(libs.paper)
}
