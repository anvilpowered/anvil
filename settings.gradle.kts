@file:Suppress("UnstableApiUsage")

dependencyResolutionManagement {
//    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven("https://libraries.minecraft.net")
        maven("https://repo.papermc.io/repository/maven-public/")
//        maven("https://repo.spongepowered.org/repository/maven-public/")
    }
}

pluginManagement {
    includeBuild("build-logic")
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "anvil"

sequenceOf(
    "agent",
    "agent-application",
    "agent-infrastructure",
    "agent-mc-sponge",
    "agent-mc-velocity",
    "cockpit",
    "cockpit-application",
    "cockpit-backend",
    "cockpit-domain",
    "cockpit-ui",
    "core",
    "core-db",
    "core-domain",
    "core-web",
).forEach {
    val project = ":anvil-$it"
    include(project)
    project(project).projectDir = file(it.replace('-', '/'))
}
