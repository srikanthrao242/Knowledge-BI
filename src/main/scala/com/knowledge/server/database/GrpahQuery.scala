package com.knowledge.server.database

import com.knowledge.server.sparkCore.SparkCoreModule
import org.apache.jena.graph.Triple
import org.apache.spark.rdd.RDD
import net.sansa_stack.query.spark.query._

class GrpahQuery extends SparkCoreModule{

  def createConfigAndRun(query : String) : Unit = {
    val data: RDD[Triple] = SPARK_CONTEXT.objectFile[Triple](CreateSchemas.getDefaultCatalog+"/*")
    val d = data.sparql(query)
    d.show()
  }

}
