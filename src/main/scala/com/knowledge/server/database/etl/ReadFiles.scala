/*

 * */
package com.knowledge.server.database.etl

import com.knowledge.server.sparkCore.SparkCoreModule
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.types.StructType

import scala.async.Async.{async, await}
import scala.collection.immutable.HashMap
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
class ReadFiles extends SparkCoreModule {

  import SPARK.implicits._
  def readJson(path: String): DataFrame =
    SPARK.read.option("multiline", "true").json(path)

  def getDataTypes(schema: StructType): Future[Map[String, String]] = async {
    val xsd = "http://www.w3.org/2001/XMLSchema#"
    var resp: Map[String, String] = HashMap[String, String]()
    schema.foreach(st_field => {
      val typeString = st_field.dataType.simpleString
      if (typeString.equalsIgnoreCase("datetime") || typeString
            .equalsIgnoreCase("timestamp")) {
        resp += (st_field.name -> "<".concat(xsd.concat("dateTime>")))
      } else if (typeString.equalsIgnoreCase("date")) {
        resp += (st_field.name -> "<".concat(xsd.concat("date>")))
      } else if (typeString.equalsIgnoreCase("int") ||
                 typeString.equalsIgnoreCase("bigint") ||
                 typeString.equalsIgnoreCase("tinyint") ||
                 typeString.equalsIgnoreCase("smallint") ||
                 typeString.equalsIgnoreCase("mediumint")) {
        resp += (st_field.name -> "<".concat(xsd.concat("integer>")))
      } else if (typeString.equalsIgnoreCase("bigint")) {
        resp += (st_field.name -> "<".concat(xsd.concat("long>")))
      } else if (typeString.equalsIgnoreCase("float")) {
        resp += (st_field.name -> "<".concat(xsd.concat("float>")))
      } else if (typeString.equalsIgnoreCase("decimal")) {
        resp += (st_field.name -> "<".concat(xsd.concat("decimal>")))
      } else {
        resp += (st_field.name -> "<".concat(xsd.concat("string>")))
      }
    })
    resp
  }

  def convertToOntology(csv: String, mappingFile: String): Unit = {
    val df = SPARK.read
      .format("csv")
      .option("sep", ",")
      .option("inferSchema", "true")
      .option("header", "true")
      .load(csv)
    val categories = df.columns
    val mappingDf = readJson(mappingFile)
    mappingDf.createOrReplaceTempView("mappingFile")
    mappingDf.printSchema()
    mappingDf.select($"relations").columns.foreach(println)
  }
}
