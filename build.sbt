name := "mini-game"

version := "0.1"


scalaVersion := "2.13.10"

val AkkaVersion = "2.7.0"
val AkkaHttpVersion = "10.4.0"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test,
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
  "org.scalatest" %% "scalatest" % "3.2.9" % "test",
  "dev.zio" %% "zio" % "2.0.0",
  "dev.zio" %% "zio-streams" % "2.0.0",
  "dev.zio" %% "zio-json" % "0.3.0",
  "io.d11" %% "zhttp" % "2.0.0-RC11",
  "com.github.andyglow" %% "websocket-scala-client" % "0.4.0" % Compile,
  "dev.zio" %% "zio-nio" % "2.0.0",
  "io.circe" %% "circe-parser" % "0.14.1"

)