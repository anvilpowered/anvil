plugins {
    id("kotlin-jvm.base-conventions")
    id("anvil-publish")
}

dependencies {
    commonMainImplementation(project(":anvil-domain"))
}
