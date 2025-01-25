val scala3Version = "3.6.2"
lazy val http4sVersion = "0.23.30"
lazy val circeVersion = "0.14.10"

Compile / run / fork := true

lazy val root = project
  .in(file("."))
  .settings(
    name := "mdfsp",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % "3.5.7",

      "org.http4s" %% "http4s-ember-client" % http4sVersion,
      "org.http4s" %% "http4s-ember-server" % http4sVersion,
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "io.circe" %% "circe-generic" % circeVersion,


      "org.scalamock" %% "scalamock" % "7.1.0" % Test,
      "org.scalameta" %% "munit" % "1.0.4" % Test
    )
  )
