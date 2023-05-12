plugins {
    id("kotlin-jvm.base-conventions")
    alias(libs.plugins.shadow)
}

dependencies {
    commonMainImplementation(project(":anvil-app-plugin-sponge"))
    commonMainImplementation(project(":anvil-app-plugin-velocity"))
}
