name := "nuXel"

organization := "ru.sggr"

version := "1.4.4"

scalaVersion := "2.11.5"

libraryDependencies += "junit" % "junit" % "4.12" % "test"

libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % Test

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"

libraryDependencies += "org.apache.poi" % "poi" % "3.14"

libraryDependencies += "org.apache.poi" % "poi-ooxml" % "3.14"

publishMavenStyle := true

resolvers += "Sonatype OSS Snapshots" at "http://maven:8081/nexus/content/repositories/snapshots"

resolvers += "Sonatype OSS Snapshots" at "http://maven:8081/nexus/content/repositories/releases"

publishTo := Some("release" at "http://maven:8081/nexus/content/repositories/releases/")

credentials += Credentials("Sonatype Nexus Repository Manager","maven",
                           "deployment",
                           "sg-gr")
