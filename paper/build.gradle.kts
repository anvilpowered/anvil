plugins {
    kotlin("jvm")
}

repositories {
    maven("https://papermc.io/repo/repository/maven-public/")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

val paperVersion: String by project

dependencies {
    implementation(project(":anvil-core"))
    implementation("io.papermc.paper:paper-api:$paperVersion")
}
