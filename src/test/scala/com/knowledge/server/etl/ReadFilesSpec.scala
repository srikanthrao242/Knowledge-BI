/*
 */
package com.knowledge.server.etl

import com.knowledge.server.database.etl.ReadFiles
import org.scalatest.FlatSpec
import org.apache.spark.sql.functions._
class ReadFilesSpec extends FlatSpec {

  trait Init {
    val rf = new ReadFiles()
  }

  "readJson" should "give the dataframe if data Exists" in new Init {
    val df = rf.readJson(
      "F:\\IdeaProjects\\Knowledge-BI\\src\\test\\resources\\oilandgasMapping.json"
    )
    df.select(explode(array(col("relations")))).columns.foreach(println)

    //df.show(false)
  }

  "checking " should "" in new Init {
    rf.convertToOntology(
      "E:\\Data\\oilandgas\\Oil_Gas_Production_All_Years.csv",
      "E:\\Data\\oilandgas\\oilandgasMapping.json"
    )
  }

}
