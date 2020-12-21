name := "autowire-experiments"

version := "0.1"

scalaVersion := "2.13.3"

val circeVersion = "0.12.3"
val autowireVersion = "0.3.2"
val upickleVersion = "0.9.5"


libraryDependencies += "com.lihaoyi" %% "autowire" % autowireVersion
libraryDependencies += "com.lihaoyi" %% "upickle" % upickleVersion
libraryDependencies ++= Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
).map(_ % circeVersion)



