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
      "org.tpolecat" %% "skunk-core" % "0.6.5",
    ) ++ Seq(
      "net.kyori" % "adventure-api",
      "net.kyori" % "adventure-text-minimessage",
    ).map(_ % "4.26.1") ++ Seq(
      "org.spongepowered" % "configurate-core",
      "org.spongepowered" % "configurate-hocon",
      "org.spongepowered" % "configurate-yaml",
    ).map(_ % "4.2.0") ++ Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser",
    ).map(_ % "0.14.15") ++ Seq(
      "co.fs2" %% "fs2-core",
      "co.fs2" %% "fs2-io",
    ).map(_ % "3.12.2") ++ Seq(
      "org.http4s" %% "http4s-core",
      "org.http4s" %% "http4s-client",
      "org.http4s" %% "http4s-server",
      "org.http4s" %% "http4s-dsl",
    ).map(_ % "0.23.33"),
  )

lazy val platformPaper = (project in file("platform/paper"))
  .settings(
    name := "platform-paper",
    libraryDependencies ++= Seq(
      "io.papermc.paper" % "paper-api" % "1.21.11-R0.1-SNAPSHOT" % "provided",
    ),
  )
  .dependsOn(core)
