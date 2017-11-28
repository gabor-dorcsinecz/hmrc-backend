name := "HMRCBackend"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq("com.typesafe.akka" %% "akka-http" % "10.0.10",
                       "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.6",
          					   "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.10",
                        "org.twitter4j" % "twitter4j-core" % "4.0.6"
)

cancelable in Global := true