/*
 */
package com.knowledge.server.etl

import com.knowledge.server.database.etl.ReadFiles
import org.scalatest.FlatSpec

class ReadFilesSpec extends FlatSpec {
  "checking " should "" in {
    val rf = new ReadFiles()
    rf.convertToOntology(
      "E:\\Data\\oilandgas\\Oil_Gas_Production_All_Years.csv",
      "E:\\Data\\oilandgas\\oilandgasMapping.json"
    )
  }

}
