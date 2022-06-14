@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.shadow)
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
    compileOnly(libs.paper)
}

/*tasks {
    shadowJar {
        dependencies {
            dependency(libs.guice)
            dependency(libs.guava)
        }
    }
}*/
