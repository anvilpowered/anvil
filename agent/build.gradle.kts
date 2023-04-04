plugins {
    id("kotlin-jvm.base-conventions")
    alias(libs.plugins.shadow)
}

dependencies {
    commonMainImplementation(project(":anvil-agent-mc-sponge"))
    commonMainImplementation(project(":anvil-agent-mc-velocity"))
}
