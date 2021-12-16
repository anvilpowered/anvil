plugins {
    kotlin("jvm")
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
    api("com.google.inject:guice:$guiceVersion")
    api("redis.clients:jedis:$jedisVersion")
    api("net.kyori:adventure-api:$kyoriVersion")
    api("net.kyori:adventure-text-feature-pagination:4.0.0-SNAPSHOT")
    api("dev.morphia.morphia:morphia-core:$morphiaVersion")
    api("org.spongepowered:configurate-core:$configurateVersion")
    api("org.spongepowered:configurate-hocon:$configurateVersion")
    api("org.spongepowered:math:$spongeMathVersion")
    api("org.jetbrains.xodus:xodus-entity-store:$xodusVersion")
    api("org.reflections:reflections:$reflectionsVersion")
    implementation("org.jetbrains:annotations:$jetbrainsAnnotationsVersion")
}
