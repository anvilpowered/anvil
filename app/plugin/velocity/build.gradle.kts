plugins {
    kotlin("kapt")
}

dependencies {
    implementation(project(":anvil-app-plugin-core"))
    implementation(project(":anvil-velocity"))
    compileOnly(libs.velocity)
    kapt(libs.velocity)
}
