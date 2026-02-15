ThisBuild / organization := "org.anvilpowered"
ThisBuild / version := "0.4.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.8.1"
ThisBuild / scalacOptions ++= Seq(
  "-Wnonunit-statement",
  "-Yexplicit-nulls",
  "-deprecation",
)

ThisBuild / resolvers ++= Seq(
  "papermc" at "https://repo.papermc.io/repository/maven-public/",
)

ThisBuild / libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect" % "3.6.3",
  "org.typelevel" %% "log4cats-slf4j" % "2.7.1",
) ++ Seq(
  "fs2-core",
  "fs2-io",
).map("co.fs2" %% _ % "3.12.2") ++ Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser",
).map(_ % "0.14.15") ++ Seq(
  "http4s-core",
  "http4s-client",
  "http4s-server",
  "http4s-dsl",
  "http4s-circe",
).map("org.http4s" %% _ % "0.23.33") ++ Seq(
  "adventure-api",
  "adventure-text-minimessage",
).map("net.kyori" % _ % "4.26.1")

lazy val root = (project in file("."))
  .settings(
    name := "anvil",
  )

lazy val libconfig = (project in file("lib/config"))
  .settings(
    name := "anvil-config",
    libraryDependencies ++= Seq(
      "configurate-core",
      "configurate-hocon",
      "configurate-yaml",
    ).map("org.spongepowered" % _ % "4.2.0"),
  )

lazy val libchat = (project in file("lib/chat"))
  .settings(
    name := "anvil-chat",
  )
//  .dependsOn(core)

lazy val libcommand = (project in file("lib/command"))
  .settings(
    name := "anvil-command",
  )
  .dependsOn(libchat)

lazy val libplatform = (project in file("lib/platform"))
  .settings(
    name := "anvil-platform",
  )
  .dependsOn(libconfig, libcommand)

val publishAllLocal = taskKey[Unit]("Publish all")
publishAllLocal := {
  (libchat / publishLocal).value
  (libcommand / publishLocal).value
  (libconfig / publishLocal).value
  (libplatform / publishLocal).value
  ()
}

//lazy val platformPaper = (project in file("platform/paper"))
//  .settings(
//    name := "platform-paper",
//    libraryDependencies ++= Seq(
//      "io.papermc.paper" % "paper-api" % "1.21.11-R0.1-SNAPSHOT" % "provided",
//    ),
//  )
//  .dependsOn(core)
