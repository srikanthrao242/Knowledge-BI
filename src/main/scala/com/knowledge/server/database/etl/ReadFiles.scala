/*

 * */
package com.knowledge.server.database.etl

import com.knowledge.server.sparkCore.SparkCoreModule
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.types.{StructField, StructType}

import scala.async.Async.async
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
class ReadFiles extends SparkCoreModule {

  val xsd = "http://www.w3.org/2001/XMLSchema#"
  import SPARK.implicits._

  def readJson(path: String): DataFrame =
    SPARK.read.option("multiline", "true").json(path)

  val isDT: String => Boolean = (typeString: String) =>
    typeString.equalsIgnoreCase("datetime") || typeString.equalsIgnoreCase(
      "timestamp"
  )

  val isDate: String => Boolean = (typeString: String) =>
    typeString.equalsIgnoreCase("date")

  val isInt: String => Boolean = (typeString: String) =>
    typeString.equalsIgnoreCase("int") ||
    typeString.equalsIgnoreCase("bigint") ||
    typeString.equalsIgnoreCase("tinyint") ||
    typeString.equalsIgnoreCase("smallint") ||
    typeString.equalsIgnoreCase("mediumint")

  val isBigInt: String => Boolean = (typeString: String) =>
    typeString.equalsIgnoreCase("bigint")

  val isFloat: String => Boolean = (typeString: String) =>
    typeString.equalsIgnoreCase("float")

  val isDecimal: String => Boolean = (typeString: String) =>
    typeString.equalsIgnoreCase("decimal")

  val fieldDTTuple: (String, String) => (String, String) =
    (name: String, dt: String) => (name -> "<".concat(xsd.concat(s"$dt>")))

  val matchDataType: StructField => (String, String) =
    (st_field: StructField) =>
      st_field.dataType.simpleString match {
        case typeString if isDT(typeString) =>
          fieldDTTuple(st_field.name, "dateTime")
        case typeString if isDate(typeString) =>
          fieldDTTuple(st_field.name, "date")
        case typeString if isInt(typeString) =>
          fieldDTTuple(st_field.name, "integer")
        case typeString if isBigInt(typeString) =>
          fieldDTTuple(st_field.name, "long")
        case typeString if isFloat(typeString) =>
          fieldDTTuple(st_field.name, "float")
        case typeString if isDecimal(typeString) =>
          fieldDTTuple(st_field.name, "decimal")
        case _ =>
          fieldDTTuple(st_field.name, "string")
    }

  def getDataTypes(schema: StructType): Future[Map[String, String]] = async {
    schema.map(matchDataType).toMap
  }

  def readCSV(csv: String): Future[DataFrame] = async {
    SPARK.read
      .format("csv")
      .option("sep", ",")
      .option("inferSchema", "true")
      .option("header", "true")
      .load(csv)
  }

  def getDfToMap(df: DataFrame): Map[String, String] = {
    val col = df.columns
    df.map { row =>
      col.map { v =>
        (v -> row.getAs[String](v))
      }.toMap
    }.collect().head
  }

  def getDfToMapArr(df: DataFrame): Map[String, Array[String]] = {
    val col = df.columns
    df.map { row =>
      col.map { v =>
        (v -> row.getAs[Array[String]](v))
      }.toMap
    }.collect().head
  }

  def convertToNQuads(csv: DataFrame,
                      prefixes: Map[String, String],
                      predicates: Map[String, String],
                      relations: Map[String, Array[String]],
                      graphName: String): Unit = async {
    val dataTypes = getDataTypes(csv.schema)
    val regex = "/(\\s)+/g".r
    csv.rdd.map(row => {
      val g_name = s"<$graphName>"

    })

  }

  def convertToOntology(csv: String, mappingFile: String): Unit = async {
    val mappingDf = readJson(mappingFile)
    val prefixes = getDfToMap(mappingDf.select("prefixes.*"))
    val predicates = getDfToMap(mappingDf.select("predicates.*"))
    val relations = getDfToMap(mappingDf.select("relations.*"))

  }
}
