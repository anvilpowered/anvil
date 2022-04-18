plugins {
    `java-library`
    kotlin("jvm")
}

version = "0.4-SNAPSHOT"

val configurateVersion: String by project
val guiceVersion: String by project
val jedisVersion: String by project
val jetbrainsAnnotationsVersion: String by project
val kotlinCoroutinesVersion: String by project
val kyoriVersion: String by project
val morphiaVersion: String by project
val reflectionsVersion: String by project
val spongeMathVersion: String by project
val xodusVersion: String by project

apply<AnvilPublishPlugin>()

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
    compileOnlyApi("com.google.inject:guice:$guiceVersion")
    api("redis.clients:jedis:$jedisVersion")
    compileOnlyApi("net.kyori:adventure-api:$kyoriVersion")
    api("net.kyori:adventure-text-feature-pagination:4.0.0-SNAPSHOT")
    api("dev.morphia.morphia:morphia-core:$morphiaVersion")
    api("org.spongepowered:configurate-core:$configurateVersion")
    api("org.spongepowered:configurate-hocon:$configurateVersion")
    api("org.spongepowered:math:$spongeMathVersion")
    api("org.jetbrains.xodus:xodus-entity-store:$xodusVersion")
    api("org.reflections:reflections:$reflectionsVersion")
    api("org.jetbrains:annotations:$jetbrainsAnnotationsVersion")
}

java {
    withSourcesJar()
    withJavadocJar()
}
