plugins {
    id("kotlin-jvm.base-conventions")
}

dependencies {
    commonMainImplementation(project(":anvil-domain"))
}
