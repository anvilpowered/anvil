import org.spongepowered.gradle.plugin.config.PluginLoaders

plugins {
    id("kotlin-jvm.base-conventions")
    alias(libs.plugins.sponge)
    alias(libs.plugins.shadow)
}

// TODO: Should not be necessary, but it seems sponge is nuking the repositories define in settings
repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    commonMainImplementation(project(":anvil-app-plugin-core"))
    commonMainImplementation(project(":anvil-infrastructure-game-sponge"))
}

sponge {
    apiVersion("8.1.0-SNAPSHOT")
    license("AGPL-3.0")
    loader {
        name(PluginLoaders.JAVA_PLAIN)
        version("1.0")
    }
    plugin("anvil-agent") {
        displayName("Anvil Agent")
        version.set(project.version.toString())
        entrypoint("org.anvilpowered.anvil.agent.AnvilSpongePlugin")
        description("Agent plugin for the Anvil system")
//        dependency("spongeapi") {
//            loadOrder(LoadOrder.AFTER)
//        }
    }
}
