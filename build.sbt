name := "akka-demo-scala"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies += "com.lightbend.akka" %% "akka-stream-alpakka-mqtt-streaming" % "1.0.1"
libraryDependencies += "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.22" % Test

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.5"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"