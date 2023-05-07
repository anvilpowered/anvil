plugins {
    id("kotlin-jvm.base-conventions")
    kotlin("kapt")
}

dependencies {
    jvmMainImplementation(project(":anvil-app-plugin"))
    jvmMainImplementation(project(":anvil-infrastructure-game-velocity"))
    jvmMainCompileOnly(libs.velocity)
    kapt(libs.velocity)
    commonMainImplementation(libs.kbrig.brigadier)
}
