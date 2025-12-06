plugins {
  alias(libs.plugins.shadow)
  alias(libs.plugins.pluginyml)
}

dependencies {
  implementation(project(":anvil-app-plugin-core"))
  implementation(project(":anvil-paper"))
  compileOnly(libs.brigadier)
  compileOnly(libs.paper)
}

paper {
  name = "anvil-agent"
  main = "org.anvilpowered.anvil.plugin.AnvilPaperPluginBootstrap"
  foliaSupported = true
  apiVersion = "1.19"
  authors = rootProject.file("authors").readLines().map { it.substringBefore(',') }
}
