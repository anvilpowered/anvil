plugins {
    id("kotlin-jvm.base-conventions")
    id("anvil-publish")
}

dependencies {
    commonMainImplementation(libs.kotlinx.coroutines)
    commonMainImplementation(project(":anvil-core"))
    jvmMainImplementation(libs.sponge)
}
