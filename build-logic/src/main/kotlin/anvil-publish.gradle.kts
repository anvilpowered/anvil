import java.net.URI

plugins {
    `maven-publish`
}

extensions.configure<PublishingExtension> {
    repositories {
        maven {
            credentials {
                username = project.findProperty("sonatypeUsername") as? String
                password = project.findProperty("sonatypePassword") as? String
            }
            val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots"
            url = URI(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
        }
    }
    publications.withType<MavenPublication> {
        pom {
            name.set("anvil")
            url.set("https://www.anvilpowered.org")
            scm {
                url.set("https://github.com/anvilpowered/anvil")
                connection.set("scm:git:https://github.com/anvilpowered/anvil.git")
                developerConnection.set("scm:git:https://github.com/anvilpowered/anvil.git")
            }
            licenses {
                license {
                    name.set("GNU AFFERO GENERAL PUBLIC LICENSE Version 3")
                    url.set("https://www.gnu.org/licenses/agpl-3.0.html")
                    distribution.set("repo")
                }
            }
            developers {
                rootProject.file("authors").readLines()
                    .asSequence()
                    .map { it.split(",") }
                    .forEach { (_id, _name) -> developer { id.set(_id); name.set(_name) } }
            }
        }
    }
}
