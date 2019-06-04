package com.knowledge.server.database

import com.knowledge.server.sparkCore.SparkCoreModule
import org.apache.jena.graph.Triple
import org.apache.spark.rdd.RDD

class GrpahQuery extends SparkCoreModule {

  def createConfigAndRun(query: String): Unit = {
    val data: RDD[Triple] = SPARK_CONTEXT.objectFile[Triple](CreateSchemas.getDefaultCatalog + "/*")
  }

}
