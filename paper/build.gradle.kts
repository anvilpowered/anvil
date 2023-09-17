plugins {
    id("anvil-publish")
}

dependencies {
    implementation(project(":anvil-core"))
    implementation(libs.kbrig.brigadier)
    compileOnly(libs.paper)
}
