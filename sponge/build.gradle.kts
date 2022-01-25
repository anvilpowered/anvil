plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

val spongeVersion: String by project

dependencies {
    implementation(project(":anvil-core"))
    implementation("org.spongepowered:spongeapi:$spongeVersion")
}

