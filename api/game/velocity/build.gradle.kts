plugins {
    id("kotlin-jvm.base-conventions")
}

dependencies {
    commonMainApi(project(":anvil-api-game"))
    commonMainImplementation(project(":anvil-infrastructure-game-velocity"))
}
