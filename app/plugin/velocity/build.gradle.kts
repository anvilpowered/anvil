plugins {
    id("kotlin-jvm.base-conventions")
    kotlin("kapt")
}

dependencies {
    commonMainImplementation(project(":anvil-app-plugin-core"))
    commonMainImplementation(project(":anvil-core-game-velocity"))
    jvmMainCompileOnly(libs.velocity)
    kapt(libs.velocity)
    commonMainImplementation(libs.kbrig.brigadier)
}
