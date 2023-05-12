plugins {
    id("kotlin-jvm.base-conventions")
    id("anvil-publish")
}

dependencies {
    commonMainApi(project(":anvil-api"))
    commonMainRuntimeOnly(project(":anvil-infrastructure-game"))
}
