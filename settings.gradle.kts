dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven("https://repo.spongepowered.org/repository/maven-public/")
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://libraries.minecraft.net/")
    }
}


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
