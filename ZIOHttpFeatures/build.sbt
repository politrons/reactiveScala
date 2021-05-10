name := "ZIOHttpFeatures"

version := "0.1"

scalaVersion := "2.13.5"

val zioVersion = "1.0.7"
val zioHttpVersion = "1.0.0.0-RC15"

libraryDependencies ++=
  Seq(
    "dev.zio" %% "zio" % zioVersion,
    "dev.zio" %% "zio-streams" % zioVersion,
    "io.netty" % "netty-all" % "4.1.63.Final",
    "org.scala-lang.modules" %% "scala-collection-compat" % "2.4.3",
    "io.d11" %% "zhttp" % zioHttpVersion
  )