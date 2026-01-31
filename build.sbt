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
).map("org.http4s" %% _ % "0.23.33")

lazy val root = (project in file("."))
  .settings(
    name := "anvil",
  )

lazy val core = (project in file("core"))
  .settings(
    name := "anvil-core",
    libraryDependencies ++= Seq(
      "org.tpolecat" %% "skunk-core" % "0.6.5",
    ),
  )

lazy val key = (project in file("lib/key"))
  .settings(
    name := "anvil-key",
    libraryDependencies ++= Seq(
      "adventure-key",
    ).map("net.kyori" % _ % "4.26.1"),
  )
lazy val config = (project in file("lib/config"))
  .settings(
    name := "anvil-config",
    libraryDependencies ++= Seq(
      "configurate-core",
      "configurate-hocon",
      "configurate-yaml",
    ).map("org.spongepowered" % _ % "4.2.0"),
  )

lazy val chat = (project in file("lib/chat"))
  .settings(
    name := "anvil-chat",
    libraryDependencies ++= Seq(
      "adventure-api",
      "adventure-text-minimessage",
    ).map("net.kyori" % _ % "4.26.1"),
  )
//  .dependsOn(core)

lazy val command = (project in file("lib/command"))
  .settings(
    name := "anvil-command",
  )
  .dependsOn(chat)

lazy val platform = (project in file("lib/platform"))
  .settings(
    name := "anvil-platform",
  )
  .dependsOn(chat)

//lazy val platformPaper = (project in file("platform/paper"))
//  .settings(
//    name := "platform-paper",
//    libraryDependencies ++= Seq(
//      "io.papermc.paper" % "paper-api" % "1.21.11-R0.1-SNAPSHOT" % "provided",
//    ),
//  )
//  .dependsOn(core)
