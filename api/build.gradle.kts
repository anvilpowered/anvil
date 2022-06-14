@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
}

version = "0.4-SNAPSHOT"

dependencies {
    api(libs.coroutines)
    api(libs.bson)
    api(libs.configurate.core)
    api(libs.flow.math)
    api(libs.guice)
    api(libs.jetbrains.annotations)
    api(libs.mongo.driver.sync)
    api(libs.morphia)
    api(libs.reflections)
    api(libs.xodus.entity.store)
}

java {
    withSourcesJar()
    withJavadocJar()
}
