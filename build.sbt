val scala3Version = "3.6.2"

Compile / run / fork := true

lazy val root = project
  .in(file("."))
  .settings(
    name := "mdfsp",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % "3.5.7",
      "org.scalamock" %% "scalamock" % "7.1.0" % Test,
      "org.scalameta" %% "munit" % "1.0.4" % Test
    )
  )
