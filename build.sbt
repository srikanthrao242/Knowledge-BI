name := "spark_rdf4j"

version := "0.1"

scalaVersion := "2.12.7"

val sparkVersion = "2.4.0"
val jenaVersion = "2.6.4"
val jenaHadoop = "3.10.0"
val scalaTest = "3.0.5"
val hadoop = "3.2.0"
val jackson = "2.8.7"


dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-core" % jackson
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % jackson
dependencyOverrides += "com.fasterxml.jackson.module" % "jackson-module-scala_2.12" % jackson

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core"      % sparkVersion ,
  "org.apache.spark" %% "spark-sql"       % sparkVersion ,
  "org.apache.spark" %% "spark-graphx"    % sparkVersion ,
  "org.apache.hadoop" % "hadoop-common" % hadoop,
  "org.apache.hadoop" % "hadoop-mapreduce-client-common" % hadoop,
  "org.apache.hadoop" % "hadoop-streaming" % hadoop,
  "com.hp.hpl.jena" % "jena" % jenaVersion,
  "org.apache.jena" % "jena-elephas-io" % jenaHadoop,
  "org.apache.jena" % "jena-elephas-mapreduce" % jenaHadoop,
  "org.scalactic" %% "scalactic" % scalaTest,
  "org.scalatest" %% "scalatest" % scalaTest % "test"
)

