import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

extensions.getByName<KotlinMultiplatformExtension>("kotlin").apply {
    js(IR) {
        browser {
            binaries.executable()
        }
    }
}
