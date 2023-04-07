import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.ktlint)
    id("kotlin-jvm.base-conventions")
}

val projectVersion = file("version").readLines().first()

allprojects {
    apply(plugin = "org.jetbrains.kotlin.multiplatform")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    group = "org.anvilpowered"
    version = projectVersion
    project.findProperty("buildNumber")
        ?.takeIf { version.toString().contains("SNAPSHOT") }
        ?.also { version = version.toString().replace("SNAPSHOT", "RC$it") }
    tasks {
        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = "17"
            kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
        }
        withType<JavaCompile> {
            options.encoding = "UTF-8"
            targetCompatibility = "17"
            sourceCompatibility = "17"
        }
    }
}
