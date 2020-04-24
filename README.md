# Anvil

[![Discord](https://img.shields.io/discord/675484700357296138)](https://discord.gg/6gR2YH3) [![TeamCity Full Build Status](https://img.shields.io/teamcity/build/e/Anvil_Build?server=http%3A%2F%2Fci.anvilpowered.org)](http://ci.anvilpowered.org/viewType.html?buildTypeId=Anvil_Build&guest=1) [![GitHub](https://img.shields.io/github/license/AnvilPowered/Anvil)](https://www.gnu.org/licenses/lgpl-3.0.html) [![Maven](https://img.shields.io/maven-central/v/org.anvilpowered/anvil-api?color=blue)](https://search.maven.org/artifact/org.anvilpowered/anvil-api)

[CI](http://ci.anvilpowered.org) (Release candidate jars)

Anvil is a mineraft plugin api that aims to help developers create structured cross-platform plugins. Included is an entity framework and many services that abstract platform-specific actions so that they can be used in common code.

Anvil is not only cross-platform in the context of plugin platforms, but also in the context of databases. Currently, MongoDB and Xodus are supported, with SQL on the way. With Anvil, you can write a central abstract set of logic that applies to several database types.

## Quick start

```groovy
repositories {
   mavenCentral();
}
dependencies {
    implementation 'org.anvilpowered:anvil-api:0.1'
}
```