/*
 */
package com.knowledge.server.etl

import com.knowledge.server.database.etl.ReadFiles
import com.knowledge.server.sparkCore.SparkCoreModule
import org.apache.spark.sql.{DataFrame, Dataset}
import org.scalatest.FlatSpec
import org.apache.spark.sql.functions._
class ReadFilesSpec extends FlatSpec with SparkCoreModule {

  trait Init {
    val rf = new ReadFiles()
  }

  "readJson" should "give the dataframe if data Exists" in new Init {
    val mappingDf: DataFrame = rf.readJson(
      "F:\\IdeaProjects\\Knowledge-BI\\src\\test\\resources\\oilandgasMapping.json"
    )

    import SPARK.implicits._
    val relations = mappingDf.select("relations.*")
    val col = relations.columns

    val res = relations.map { row =>
      col.map { v =>
        (v -> row.getAs[Array[String]](v))
      }.toMap
    }.collect().head

    print(res)

  }

  "checking " should "" in new Init {
    rf.convertToOntology(
      "E:\\Data\\oilandgas\\Oil_Gas_Production_All_Years.csv",
      "E:\\Data\\oilandgas\\oilandgasMapping.json"
    )
  }

}
