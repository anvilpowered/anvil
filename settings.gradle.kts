/*pluginManagement {
    plugins {
        val blossomVersion: String by settings
        val kotlinVersion: String by settings
        val shadowVersion: String by settings
        id("net.kyori.blossom") version blossomVersion
        kotlin("jvm") version kotlinVersion
        id("com.github.johnrengelman.shadow") version shadowVersion
    }
}*/

rootProject.name = "Anvil"

include("anvil-md5")

sequenceOf(
    "api",
    "core",
    "paper",
    "sponge",
    "velocity"
).forEach {
    val project = ":anvil-$it"
    include(project)
    project(project).projectDir = file(it)
}
