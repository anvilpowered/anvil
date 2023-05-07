plugins {
    id("kotlin-jvm.base-conventions")
}

dependencies {
    commonMainImplementation(project(":anvil-infrastructure-game"))
    commonMainImplementation(libs.kbrig.brigadier)
    jvmMainCompileOnly(libs.velocity)
}
