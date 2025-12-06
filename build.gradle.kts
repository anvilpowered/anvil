import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.ktlint)
  alias(libs.plugins.kotlin.serialization)
}

val rawVersion: Provider<String> = providers.fileContents(layout.projectDirectory.file("version")).asText.map { it.trim() }

val vcsBuildVersion: Provider<String> =
  provider {
    System.getenv("VCS_BRANCH")?.replace('/', '-') ?: "unknown-branch"
  }.flatMap { branch ->
    System.getenv("BUILD_NUMBER")?.let { buildNumber ->
      providers
        .exec {
          commandLine("git", "rev-parse", "--short", "HEAD")
        }.standardOutput.asText
        .flatMap { gitRev ->
          rawVersion.map { it.replace("SNAPSHOT", "BETA$buildNumber-$branch-${gitRev.trim()}") }
        }
    } ?: rawVersion
  }

val isRawVersion: Provider<Boolean> = provider { project.hasProperty("rawVersion") }

val projectVersion: Provider<String> =
  isRawVersion.zip(
    rawVersion.zip(vcsBuildVersion) { raw, build -> raw to build },
  ) { isRaw, versions -> if (isRaw) versions.first else versions.second }

logger.warn("Resolved project version ${projectVersion.get()}")

allprojects {
  apply(plugin = "org.jetbrains.kotlin.jvm")
  apply(plugin = "org.jlleitschuh.gradle.ktlint")
  apply(plugin = "java-library")

  group = "org.anvilpowered"
  version = projectVersion.get()

  kotlin {
    compilerOptions {
      jvmTarget = JvmTarget.JVM_21
      freeCompilerArgs =
        listOf(
          "-opt-in=kotlin.RequiresOptIn",
          "-Xcontext-receivers",
        )
    }
  }

  java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
  }
}
