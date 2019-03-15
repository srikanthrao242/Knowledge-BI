package com.knowledge.server.sansa

import com.knowledge.server.sparkCore.SparkCoreModule
import org.apache.spark.rdd.RDD
import org.apache.jena.graph.Triple
import net.sansa_stack.rdf.spark.io._
import net.sansa_stack.query.spark.query._
import net.sansa_stack.rdf.spark.stats._
import net.sansa_stack.rdf.spark.model._
import org.apache.spark.sql.DataFrame
import com.knowledge.server.database.graphx.{SparkGraph, StatisticsRDF}

class Measures(data:Either[List[Triple], RDD[Triple]]) extends SparkCoreModule{

  import SPARK.implicits._
  //import SPARK.sqlContext.implicits._

  val rdd = data match {
    case Left(s) => SPARK_CONTEXT.parallelize(s)
    case Right(r)=> r
  }

  rdd.stats

  //println(StatisticsRDF.AvgPerProperty(rdd).count())
  /*rdd.foreach(v=>{
    println(v.getObject.getLiteralDatatype.getURI)
  })
*/

  StatisticsRDF.AvgPerPredicate(rdd)

  //println(StatisticsRDF.MaxPerProperty(rdd))




}
