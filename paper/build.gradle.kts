plugins {
    kotlin("jvm")
}

repositories {
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    implementation(project(":anvil-core"))
}
