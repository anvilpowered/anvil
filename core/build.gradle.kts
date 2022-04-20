import java.text.SimpleDateFormat
import java.util.TimeZone
import java.util.Date

plugins {
    kotlin("jvm")
    id("net.kyori.blossom")
}

val configurate_hocon: String by project
val reflections: String by project

dependencies {
    api(project(":anvil-api"))
    api(kotlin("reflect"))
    api(configurate_hocon)
    implementation(reflections)
}

blossom {
    replaceToken("{modVersion}", version)
    val format = SimpleDateFormat("yyyy-MM-dd-HH:mm:ss z")
    format.timeZone = TimeZone.getTimeZone("UTC")
    val buildDate = format.format(Date())
    replaceToken("{buildDate}", buildDate)
}
