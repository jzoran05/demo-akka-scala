name := "akka-demo-scala"

version := "0.1"

scalaVersion := "2.12.8"
val scalaDependencyVersion = "2.5.23"
val alpakkaDependencyVersion = "1.0.4"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % scalaDependencyVersion
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % scalaDependencyVersion
libraryDependencies += "com.lightbend.akka" %% "akka-stream-alpakka-mqtt-streaming" % "1.0.1"
libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka" % alpakkaDependencyVersion
libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka-testkit" % alpakkaDependencyVersion
libraryDependencies += "com.typesafe.akka" %% "akka-stream-testkit" % scalaDependencyVersion % Test

libraryDependencies += "org.testcontainers" % "kafka" % "1.11.2"
libraryDependencies += "org.apache.commons" % "commons-compress" % "1.18" // embedded Kafka pulls in Avro which pulls in commons-compress 1.8.1

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.8"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % "test"