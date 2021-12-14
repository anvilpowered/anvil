import java.text.SimpleDateFormat

plugins {
    kotlin("jvm")
}

val javasisstVersion: String by project

dependencies {
    api(project(":anvil-api"))
    api(kotlin("reflect"))
    api("org.javassist:javassist:$javasisstVersion")
}
