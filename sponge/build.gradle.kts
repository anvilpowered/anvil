plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

dependencies {
    implementation(project(":anvil-core"))
    compileOnly("org.spongepowered:spongeapi:7.4.0")
}
