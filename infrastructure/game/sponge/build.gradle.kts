plugins {
    id("kotlin-jvm.base-conventions")
    id("anvil-publish")
}

dependencies {
//    commonMainImplementation(libs.coroutines)
    commonMainImplementation(libs.kotlinx.coroutines)
    commonMainImplementation(project(":anvil-infrastructure-game"))
    jvmMainImplementation(libs.sponge)
}
