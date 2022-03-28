/*
 *   Jagr - SourceGrade.org
 *   Copyright (C) 2021-2022 Alexander Staeding
 *   Copyright (C) 2021-2022 Contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.plugins.signing.SigningExtension
import org.gradle.plugins.signing.SigningPlugin
import java.net.URI

class AnvilPublishPlugin : Plugin<Project> {
  override fun apply(target: Project) = target.afterEvaluate { configure() }
  private fun Project.configure() {
    apply<JavaBasePlugin>()
    apply<MavenPublishPlugin>()
    apply<SigningPlugin>()
    extensions.configure<JavaPluginExtension> {
      withJavadocJar()
      withSourcesJar()
    }
    extensions.configure<PublishingExtension> {
      repositories {
        maven {
          credentials {
            username = project.findProperty("sonatypeUsername") as? String
            password = project.findProperty("sonatypePassword") as? String
          }
          val releasesRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
          val snapshotsRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots"
          url = URI(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
        }
      }
      publications {
        create<MavenPublication>("maven") {
          from(components["java"])
          pom {
            name.set("Anvil")
            description.set("A cross-platform database API / ORM / entity framework with useful services for minecraft plugins")
            url.set("https://www.anvilpowered.org")
            scm {
              url.set("https://github.com/AnvilPowered/Anvil")
              connection.set("scm:git:https://github.com/AnvilPowered/Anvil.git")
              developerConnection.set("scm:git:https://github.com/AnvilPowered/Anvil.git")
            }
            developers {
              developer {
                id.set("alexstaeding")
                name.set("Alexander Staeding")
              }
            }
          }
        }
      }
    }
    extensions.configure<SigningExtension> {
      sign(extensions.getByType<PublishingExtension>().publications)
    }
  }
}
