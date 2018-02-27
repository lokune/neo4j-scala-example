name := """demo-service"""
organization := "com.okune"
version := "1.0.0-SNAPSHOT"
scalaVersion := "2.12.4"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaHttpV = "10.0.11"
  val scalaTestV = "3.0.1"
  val henkanV = "0.6.1"
  Seq(
    "com.typesafe.akka" %% "akka-http" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
    "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
    "com.typesafe" % "config" % "1.3.2",
    "com.michaelpollmeier" %% "gremlin-scala" % "3.3.1.1",
    "com.steelbridgelabs.oss" % "neo4j-gremlin-bolt" % "0.2.27",
    "com.fasterxml.uuid" % "java-uuid-generator" % "3.1.4", /* performs better in generating uuids */
    "com.wix" %% "accord-core" % "0.7.2",
    "com.kailuowang" %% "henkan-convert" % henkanV,
    "com.kailuowang" %% "henkan-optional" % henkanV,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV % Test,
    "org.scalatest" %% "scalatest" % scalaTestV % Test
  )
}

parallelExecution in Test := false

assemblyMergeStrategy in assembly := {
  case PathList(ps@_*) if ps.last endsWith ".class" => MergeStrategy.first
  case PathList(ps@_*) if ps.last endsWith ".java" => MergeStrategy.first
  case PathList(ps@_*) if ps.last endsWith ".css" => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}
