ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

val AkkaVersion = "2.8.0"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test,
  "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
  //"com.typesafe.akka" %% "akka-slf4j_2.12" % AkkaVersion
  "ch.qos.logback" % "logback-classic" % "1.1.3" % Runtime
)

lazy val root = (project in file("."))
  .settings(
    name := "simple-akka"
  )
