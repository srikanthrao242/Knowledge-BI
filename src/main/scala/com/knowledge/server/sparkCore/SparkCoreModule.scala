package com.knowledge.server.sparkCore

import java.io.File

import org.apache.spark.sql.SparkSession


/**
  * Created by srikanth on 5/20/18.
  */
trait SparkCoreModule {
  private val dir = new File("spark-warehouse")
  private val successful = dir.mkdir()
  private var warehouseLocation = new File("spark-warehouse").getAbsolutePath
  if (successful) warehouseLocation = dir.getAbsolutePath
  final implicit lazy val SPARK = SparkSession
    .builder()
    .master("local[4]")
    .appName("Knowlede-BI")
    .config("spark.sql.warehouse.dir", warehouseLocation)
    .config("spark.sql.sources.maxConcurrentWrites", "1")
    .config("spark.sql.parquet.compression.codec", "snappy")
    .config("hive.exec.dynamic.partition", "true")
    .config("hive.exec.dynamic.partition.mode", "nonstrict")
    .config("parquet.compression", "SNAPPY")
    .config("hive.exec.max.dynamic.partitions", "3000")
    .config("parquet.enable.dictionary", "false")
    .config("hive.support.concurrency", "true")
    .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    .config("spark.kryo.registrator", String.join(
      ", ",
      "net.sansa_stack.rdf.spark.io.JenaKryoRegistrator",
      "net.sansa_stack.query.spark.sparqlify.KryoRegistratorSparqlify"))
    //.enableHiveSupport()
    .getOrCreate()
  final implicit lazy val SPARK_CONTEXT = SPARK.sparkContext
}
