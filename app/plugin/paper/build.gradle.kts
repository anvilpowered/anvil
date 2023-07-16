plugins {
    id("kotlin-jvm.base-conventions")
    alias(libs.plugins.shadow)
    alias(libs.plugins.pluginyml)
}

dependencies {
    commonMainImplementation(project(":anvil-app-plugin-core"))
    commonMainImplementation(project(":anvil-core-game-paper"))
    jvmMainCompileOnly(libs.brigadier)
    jvmMainCompileOnly(libs.paper)
}

paper {
    main = "org.anvilpowered.anvil.plugin.AnvilPaperPluginBootstrap"
    foliaSupported = true
    apiVersion = "1.19"
    authors = rootProject.file("authors").readLines().map { it.substringBefore(',') }
}
