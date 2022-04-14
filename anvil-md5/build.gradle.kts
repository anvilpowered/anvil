plugins {
    java
}

val bungee: String by project

dependencies {
    api(project(":anvil-core"))
    implementation(bungee)
}
