import java.text.SimpleDateFormat
import java.util.TimeZone
import java.util.Date

plugins {
    kotlin("jvm")
    id("net.kyori.blossom")
}

val javasisstVersion: String by project

dependencies {
    api(project(":anvil-api"))
    api(kotlin("reflect"))
    api("org.javassist:javassist:$javasisstVersion")
}

blossom {
    replaceToken("{modVersion}", version)
    val format = SimpleDateFormat("yyyy-MM-dd-HH:mm:ss z")
    format.timeZone = TimeZone.getTimeZone("UTC")
    val buildDate = format.format(Date())
    replaceToken("{buildDate}", buildDate)
}
