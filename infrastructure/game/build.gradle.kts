plugins {
    id("kotlin-jvm.base-conventions")
    id("anvil-publish")
}

dependencies {
    commonMainApi(project(":anvil-infrastructure"))
    commonMainApi(project(":anvil-api-game"))
}
