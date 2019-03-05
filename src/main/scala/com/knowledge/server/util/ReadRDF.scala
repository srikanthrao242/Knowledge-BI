package com.knowledge.server.util

import com.knowledge.server.sparkCore.SparkCoreModule
import net.sansa_stack.rdf.spark.io._
import org.apache.jena.graph.Triple
import org.apache.jena.riot.Lang
import org.apache.spark.rdd.RDD

import scala.concurrent.ExecutionContext.Implicits.global
import scala.async.Async.{async, await}
import scala.concurrent.Future

/**
  * Created by srikanth on 11/3/18.
  */
object ReadRDF extends SparkCoreModule{

  def readNtriples(input:String):Future[RDD[Triple]]= async {
    val lang = Lang.NTRIPLES
    SPARK.rdf(lang)(input)
  }

  def main(args: Array[String]): Unit = {
    readNtriples("./datasets/rdf.nt")
  }

}
