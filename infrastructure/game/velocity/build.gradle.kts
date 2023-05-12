plugins {
    id("kotlin-jvm.base-conventions")
    id("anvil-publish")
}

dependencies {
    commonMainImplementation(project(":anvil-infrastructure-game"))
    commonMainImplementation(libs.kbrig.brigadier)
    jvmMainCompileOnly(libs.velocity)
}
