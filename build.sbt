name := "HMRCBackend"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.10",
  "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.6",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.10",
  "org.twitter4j" % "twitter4j-core" % "4.0.6",
  "org.scalikejdbc" %% "scalikejdbc" % "3.1.0",
  "com.h2database" % "h2" % "1.4.196",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)

cancelable in Global := true