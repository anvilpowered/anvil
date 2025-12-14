ThisBuild / version := "0.4.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.7.4"
ThisBuild / scalacOptions += "-Wnonunit-statement"

lazy val root = (project in file("."))
  .settings(
    name := "anvil",
  )

lazy val core = (project in file("core"))
  .settings(
    name := "anvil-core",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % "3.6.3",
      "org.typelevel" %% "log4cats-slf4j" % "2.7.1",
      "net.kyori" % "adventure-api" % "4.25.0",
    ) ++ Seq(
      "org.spongepowered" % "configurate-core",
      "org.spongepowered" % "configurate-hocon",
      "org.spongepowered" % "configurate-yaml",
    ).map(_ % "4.2.0") ++ Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser",
    ).map(_ % "0.14.15"),
  )
