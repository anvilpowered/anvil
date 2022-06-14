import java.text.SimpleDateFormat
import java.util.TimeZone
import java.util.Date

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.jvm)
    //id("net.kyori.blossom")
}

dependencies {
    api(project(":anvil-api"))
    api(libs.kotlin.reflect)
    api(libs.configurate.hocon)
    implementation(libs.reflections)
}

/*blossom {
    replaceToken("{modVersion}", version)
    val format = SimpleDateFormat("yyyy-MM-dd-HH:mm:ss z")
    format.timeZone = TimeZone.getTimeZone("UTC")
    val buildDate = format.format(Date())
    replaceToken("{buildDate}", buildDate)
}*/
