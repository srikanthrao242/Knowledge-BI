/*   
* egal Notice
 *
 * Confidential and Proprietary materials of srikanth rao.
 *
 * Copyright (c) 2019, all rights reserved.
 *
* */

name := "Knowledge-BI"

version := "1.0"

scalaVersion := "2.12.8"

val sparkVersion = "2.4.3"
val sansaVersion = "0.4.0"

dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-core" % "2.6.5"
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.5"
dependencyOverrides += "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.6.5"

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-compiler" % scalaVersion.value,
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-graphx" % sparkVersion,
  "com.franz" % "agraph-java-client" % "2.2.1",
  "net.jpountz.lz4" % "lz4" % "1.3.0"
)

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)

lazy val excludeSpark = ExclusionRule(organization = "org.apache.spark")
lazy val excludeScalaCom = ExclusionRule(organization = "org.scala-lang.modules")
lazy val excludeScalaNlp = ExclusionRule(organization = "org.scalanlp")
lazy val excludeScalaSpireMath = ExclusionRule(organization = "org.spire-math")
lazy val excludeSparkBench = ExclusionRule(organization = "com.ibm.sparktc.sparkbench")
lazy val excludeShapeless = ExclusionRule(organization = "com.chuusai")
lazy val excludeTypeLevel = ExclusionRule(organization = "org.typelevel")
lazy val excludeJacksonCore = ExclusionRule(organization = "com.fasterxml.jackson.core")
lazy val excludeJacksonModule = ExclusionRule(organization = "com.fasterxml.jackson.module")
lazy val excludeTest = ExclusionRule(organization = "org.scalatest")
lazy val excludeTest1 = ExclusionRule(organization = "org.scalactic")

val excludeInSansa =
  List(excludeSpark, excludeScalaCom, excludeSparkBench, excludeShapeless, excludeTypeLevel, excludeTest, excludeTest1)

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-library" % scalaVersion.value,
  "com.intel.analytics.bigdl" % "bigdl-SPARK_2.2" % "0.4.0" excludeAll (excludeScalaNlp, excludeScalaSpireMath),
  "javax.ws.rs" % "javax.ws.rs-api" % "2.1" artifacts Artifact("javax.ws.rs-api", "jar", "jar")
)

// | Extra libraries

resolvers ++= Seq(
  "AKSW Maven Releases" at "http://maven.aksw.org/archiva/repository/internal",
  "AKSW Maven Snapshots" at "http://maven.aksw.org/archiva/repository/snapshots",
  "oss-sonatype" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Apache repository (snapshots)" at "https://repository.apache.org/content/repositories/snapshots/"
)

resolvers ++= Seq(
  "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
  "NetBeans" at "http://bits.netbeans.org/nexus/content/groups/netbeans/",
  "gephi" at "https://raw.github.com/gephi/gephi/mvn-thirdparty-repo/"
)

// Use local repositories by default
resolvers ++= Seq(
  Resolver.defaultLocal,
  Resolver.mavenLocal,
  "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
  "Apache Staging" at "https://repository.apache.org/content/repositories/staging/"
)

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-log4j12" % "1.7.25",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.scalafx" %% "scalafx" % "12.0.1-R17",
  "org.scalafx" %% "scalafxml-core-sfx8" % "0.4",
  "org.scala-lang.modules" %% "scala-async" % "0.10.0",
  "io.spray" %% "spray-json" % "1.3.5",
  "org.scalatest" %% "scalatest" % "3.2.0-SNAP10" % Test,
  "org.scalacheck" %% "scalacheck" % "1.14.0" % Test,
  "net.sansa-stack" % "sansa-owl-spark_2.11" % sansaVersion excludeAll (excludeInSansa: _*),
  "net.sansa-stack" % "sansa-inference-spark_2.11" % sansaVersion excludeAll (excludeInSansa: _*),
  "net.sansa-stack" % "sansa-query-spark_2.11" % sansaVersion excludeAll (excludeInSansa: _*),
  "net.sansa-stack" % "sansa-ml-spark_2.11" % sansaVersion excludeAll (excludeInSansa: _*),
  "net.sansa-stack" % "sansa-rdf-spark_2.11" % sansaVersion excludeAll (excludeInSansa: _*)
)

fork := true
shellPrompt := { state =>
  System.getProperty("user.name") + s":${name.value}> "
}
