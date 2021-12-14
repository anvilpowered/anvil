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
val spongeMathVersion: String by project
val xodusVersion: String by project

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
    api("org.spongepowered:configurate-core:$configurateVersion")
    api("com.google.inject:guice:$guiceVersion")
    api("redis.clients:jedis:$jedisVersion")
    api("net.kyori:adventure-api:$kyoriVersion")
    api("dev.morphia.morphia:morphia:$morphiaVersion")
    api("org.spongepowered:math:$spongeMathVersion")
    api("org.jetbrains.xodus:xodus-entity-store:$xodusVersion")
    implementation("org.jetbrains:annotations:$jetbrainsAnnotationsVersion")
}
