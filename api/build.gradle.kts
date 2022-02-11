plugins {
    `java-library`
    kotlin("jvm")
    `maven-publish`
}

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

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
    compileOnlyApi("com.google.inject:guice:$guiceVersion")
    api("redis.clients:jedis:$jedisVersion")
    compileOnlyApi("net.kyori:adventure-api:$kyoriVersion")
    api("net.kyori:adventure-text-feature-pagination:4.0.0-SNAPSHOT")
    api("dev.morphia.morphia:morphia-core:$morphiaVersion")
    compileOnlyApi("org.spongepowered:configurate-core:$configurateVersion")
    compileOnlyApi("org.spongepowered:configurate-hocon:$configurateVersion")
    compileOnlyApi("org.spongepowered:math:$spongeMathVersion")
    api("org.jetbrains.xodus:xodus-entity-store:$xodusVersion")
    compileOnlyApi("org.reflections:reflections:$reflectionsVersion")
    api("org.jetbrains:annotations:$jetbrainsAnnotationsVersion")
    api("org.anvilpowered:registry:0.1.0-SNAPSHOT")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}
