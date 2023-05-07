plugins {
    id("kotlin-jvm.base-conventions")
}

dependencies {
    commonMainApi(project(":anvil-infrastructure"))
    commonMainApi(project(":anvil-api-game"))
}
