plugins {
    id("kotlin-js.base-conventions")
}

fun kotlinw(target: String): String =
    "org.jetbrains.kotlin-wrappers:kotlin-$target"

dependencies {
    commonMainImplementation(project(":anvil-domain"))
    commonMainImplementation(libs.redux)
    jsMainImplementation(enforcedPlatform(kotlinw("wrappers-bom:1.0.0-pre.480")))

    jsMainImplementation(kotlinw("react"))
    jsMainImplementation(kotlinw("react-dom"))
    jsMainImplementation(kotlinw("react-router-dom"))

    jsMainImplementation(kotlinw("emotion"))
    jsMainImplementation(kotlinw("mui"))
    jsMainImplementation(kotlinw("mui-icons"))

    jsMainImplementation(npm("date-fns", "2.29.3"))
    jsMainImplementation(npm("@date-io/date-fns", "2.16.0"))
}
