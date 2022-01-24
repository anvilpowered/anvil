plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.kapt")
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

repositories {
    maven("https://libraries.minecraft.net/")
    maven("https://repo.velocitypowered.com/snapshots/")
}

val velocityVersion: String by project

dependencies {
    implementation(project(":anvil-core"))
    implementation("com.velocitypowered:velocity-api:$velocityVersion")
    kapt("com.velocitypowered:velocity-api:$velocityVersion")
}

tasks.shadowJar {
    val configurateVersion: String by project
    val spongeMathVersion: String by project
    val kyoriVersion: String by project
    val guiceVersion: String by project

    dependencies {
        exclude(dependency("ninja.leaping:configurate"))
        exclude(dependency("io.github:classgraph"))
        exclude(dependency("io.leangen:geantyref"))
        exclude(dependency("org.slf4j:slf4j"))
        exclude(dependency("com.google.inject:guice"))
        exclude(dependency("net.kyori:adventure-api"))
        exclude(dependency("net.kyori:adventure"))
        exclude(dependency("net.kyori:examination"))
        exclude(dependency("com.velocitypowered:velocity-api"))
    }
}