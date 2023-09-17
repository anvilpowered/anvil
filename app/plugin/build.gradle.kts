plugins {
    alias(libs.plugins.shadow)
}

dependencies {
    implementation(project(":anvil-app-plugin-paper"))
    implementation(project(":anvil-app-plugin-sponge"))
    implementation(project(":anvil-app-plugin-velocity"))
}

tasks {
    shadowJar {
        archiveFileName = "anvil-agent-${project.version}.jar"
    }
}
