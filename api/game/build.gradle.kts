plugins {
    id("kotlin-jvm.base-conventions")
}

dependencies {
    commonMainApi(project(":anvil-api"))
    commonMainRuntimeOnly(project(":anvil-infrastructure-game"))
}
