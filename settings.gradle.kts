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
    "app-plugin",
    "app-plugin-core",
    "app-plugin-paper",
    "app-plugin-sponge",
    "app-plugin-velocity",
    "core",
    "paper",
    "sponge",
    "velocity",
).forEach {
    val project = ":anvil-$it"
    include(project)
    project(project).projectDir = file(it.replace('-', '/'))
}
