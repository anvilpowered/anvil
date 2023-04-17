plugins {
    id("kotlin-jvm.base-conventions")
    kotlin("kapt")
}

dependencies {
    jvmMainApi(project(":anvil-agent-application"))
    jvmMainCompileOnly(libs.velocity)
    kapt(libs.velocity)
    commonMainImplementation(libs.kbrig.brigadier)
}
