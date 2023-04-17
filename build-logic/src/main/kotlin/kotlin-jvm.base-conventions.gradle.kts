import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

extensions.getByName<KotlinMultiplatformExtension>("kotlin").apply {
    jvm {
        withJava()
        compilations.all {
            jvmToolchain(17)
        }
    }
}
