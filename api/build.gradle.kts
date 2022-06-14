plugins {
    `java-library`
    kotlin("jvm")
}

version = "0.4-SNAPSHOT"

val bson: String by project
val configurate_core: String by project
val flow_math: String by project
val guice: String by project
val guice_version: String by project
val jetbrains_annotations: String by project
val mongodb_driver_sync: String by project
val morphia: String by project
val reflections: String by project
val xodus_entity_store: String by project

dependencies {
    api(bson)
    api(configurate_core)
    api(flow_math)
    api(guice + ":" + guice_version)
    api(jetbrains_annotations)
    api(mongodb_driver_sync)
    api(morphia)
    api(reflections)
    api(xodus_entity_store)
}

java {
    withSourcesJar()
    withJavadocJar()
}
