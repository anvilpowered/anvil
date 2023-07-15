plugins {
    id("kotlin-jvm.base-conventions")
    id("anvil-publish")
}

dependencies {
    commonMainApi(project(":anvil-core"))
    commonMainApi(project(":anvil-core-db"))
    commonMainApi(project(":anvil-api"))
}