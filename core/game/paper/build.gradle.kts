plugins {
    id("kotlin-jvm.base-conventions")
    id("anvil-publish")
}

dependencies {
    commonMainImplementation(project(":anvil-core-game"))
    commonMainImplementation(libs.kbrig.brigadier)
    jvmMainCompileOnly(libs.paper)
}
