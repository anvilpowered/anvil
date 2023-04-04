plugins {
    id("kotlin-jvm.base-conventions")
    kotlin("kapt")
}

dependencies {
    commonMainApi(project(":anvil-agent-application"))
    jvmMainImplementation(libs.velocity)
    kapt(libs.velocity)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}
