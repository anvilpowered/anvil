plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

repositories {
    maven("https://papermc.io/repo/repository/maven-public/")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation(project(":anvil-md5"))
    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
}

tasks.shadowJar {
    val guice: String by project
    val guava: String by project
    dependencies {
        dependency(guice)
        dependency(guava)
    }
}
