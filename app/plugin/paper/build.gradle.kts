plugins {
    id("kotlin-jvm.base-conventions")
    alias(libs.plugins.shadow)
}

dependencies {
    commonMainImplementation(project(":anvil-app-plugin-core"))
    commonMainImplementation(project(":anvil-core-game-paper"))
    jvmMainCompileOnly(libs.brigadier)
    jvmMainCompileOnly(libs.paper)
}
