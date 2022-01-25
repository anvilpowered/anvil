plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "5.2.0"
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
    compileOnly("io.papermc.paper:paper-api:$paperVersion")
}

tasks.shadowJar {
    val guice: String by project
    val guava: String by project
    dependencies {
        dependency(guice)
        dependency(guava)
    }
}
