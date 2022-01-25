rootProject.name = "Anvil"

include(":anvil-api")
include(":anvil-core")
include(":anvil-paper")
include(":anvil-sponge")
include(":anvil-velocity")

project(":anvil-api").projectDir = File("api")
project(":anvil-core").projectDir = File("core")
project(":anvil-paper").projectDir = File("paper")
project(":anvil-sponge").projectDir = File("sponge")
project(":anvil-velocity").projectDir = File("velocity")

pluginManagement {
    plugins {
        val kotlinVersion: String by settings
        kotlin("jvm") version kotlinVersion
        id("net.kyori.blossom") version "1.3.0"
    }
}
