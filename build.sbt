name := "MyScalaWorkshop"

version := "0.1"

scalaVersion := "2.13.10"


val akkaVersion = "2.6.20"

lazy val akkaDeps = Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion
)

libraryDependencies ++= akkaDeps