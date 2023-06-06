plugins {
    id("kotlin-jvm.base-conventions")
    id("kotlin-js.base-conventions")
    id("anvil-publish")
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    commonMainApi("org.lighthousegames:logging:1.3.0")
//    commonMainImplementation(libs.logging)
    commonMainApi(project(":anvil-domain"))
}
