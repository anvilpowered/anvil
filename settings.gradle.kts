@file:Suppress("UnstableApiUsage")

dependencyResolutionManagement {
//    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://libraries.minecraft.net")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.spongepowered.org/repository/maven-public/")
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
    "api",
    "api-web",
    "app-plugin",
    "app-plugin-core",
    "app-plugin-paper",
    "app-plugin-sponge",
    "app-plugin-velocity",
    "app-cockpit",
    "app-cockpit-application",
    "app-cockpit-backend",
    "app-cockpit-domain",
    "app-cockpit-ui",
    "app-cli",
    "domain",
    "core",
    "core-db",
    "core-game",
    "core-game-paper",
    "core-game-sponge",
    "core-game-velocity",
).forEach {
    val project = ":anvil-$it"
    include(project)
    project(project).projectDir = file(it.replace('-', '/'))
}
