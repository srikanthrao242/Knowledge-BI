name := "Knowledge-BI"

version := "1.0"

scalaVersion := "2.11.11"

val varscalaVersion = "2.11.11"
val varscalaBinaryVersion = "2.11"
val sansaVersion = "0.4.0"

val sparkVersion = "2.4.0"


dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-core" % "2.8.7"
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.7"
dependencyOverrides += "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.8.7"

lazy val excludeJpountz = ExclusionRule(organization = "net.jpountz.lz4", name = "lz4")


libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core"      % sparkVersion excludeAll excludeJpountz ,
  "org.apache.spark" %% "spark-sql"       % sparkVersion excludeAll excludeJpountz ,
  "org.apache.spark" %% "spark-graphx"    % sparkVersion excludeAll excludeJpountz ,
  "com.franz" % "agraph-java-client" % "2.2.1",
  "net.jpountz.lz4" % "lz4" % "1.3.0"
)

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-library" % varscalaVersion,
  "com.intel.analytics.bigdl" % "bigdl-SPARK_2.2" % "0.4.0",
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
  "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/", "NetBeans" at "http://bits.netbeans.org/nexus/content/groups/netbeans/", "gephi" at "https://raw.github.com/gephi/gephi/mvn-thirdparty-repo/" )

// Use local repositories by default
resolvers ++= Seq(
  Resolver.defaultLocal,
  Resolver.mavenLocal,
  "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository",
  "Apache Staging" at "https://repository.apache.org/content/repositories/staging/"
)

// | SANSA Layers
libraryDependencies ++= Seq(
  ("net.sansa-stack" %% "sansa-rdf-spark" % sansaVersion)
    .exclude("com.ibm.sparktc.sparkbench","sparkbench"),
  "net.sansa-stack" %% "sansa-owl-spark" % sansaVersion,
  "net.sansa-stack" %% "sansa-inference-spark" % sansaVersion,
  "net.sansa-stack" %% "sansa-query-spark" % sansaVersion,
  ("net.sansa-stack" %% "sansa-ml-spark" % sansaVersion)
    .exclude("com.ibm.sparktc.sparkbench","sparkbench"),
  "org.slf4j" % "slf4j-log4j12" % "1.7.25",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.scalafx" %% "scalafx" % "8.0.102-R11",
  "org.scalafx" %% "scalafxml-core-sfx8" % "0.4",
  "org.scala-lang.modules" %% "scala-async" % "0.9.7",
  /*"org.abego.treelayout" % "org.abego.treelayout.core" % "1.0.3",
  "org.prefuse" % "prefuse" % "beta-20071021",*/
  "org.specs2" %% "specs2" % "3.7" % Test pomOnly()
)

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

// Fork a new JVM for 'run' and 'test:run', to avoid JavaFX double initialization problems
fork := true

shellPrompt := { state => System.getProperty("user.name") + s":${name.value}> " }