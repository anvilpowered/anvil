@file:Suppress("UnstableApiUsage")

dependencyResolutionManagement {
//    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
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
    "agent-sponge",
    "agent-velocity",
    "domain",
    "server",
    "server-impl",
    "ui",
).forEach {
    val project = ":anvil-$it"
    include(project)
    project(project).projectDir = file(it.replace('-', '/'))
}
