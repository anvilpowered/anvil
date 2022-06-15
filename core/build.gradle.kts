import java.text.SimpleDateFormat
import java.util.TimeZone
import java.util.Date

plugins {
    kotlin("jvm")
}

val configurate_hocon: String by project
val reflections: String by project

dependencies {
    api(project(":anvil-api"))
    api(kotlin("reflect"))
    api(configurate_hocon)
    implementation(reflections)
}
