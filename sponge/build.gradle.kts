plugins {
    kotlin("jvm")
}

val spongeVersion: String by project

dependencies {
    implementation(project(":anvil-core"))
    implementation("org.spongepowered:spongeapi:$spongeVersion")
}
