/**/
package com.knowledge.server.util

import com.knowledge.server.sparkCore.SparkCoreModule
import org.apache.jena.graph.Triple
import org.apache.jena.riot.Lang
import org.apache.spark.rdd.RDD

import scala.concurrent.ExecutionContext.Implicits.global
import scala.async.Async.{async, await}
import scala.concurrent.Future
import net.sansa_stack.rdf.spark.io._

/**
  * Created by srikanth on 11/3/18.
  */
object ReadRDF extends SparkCoreModule {

  def readNtriples(input: String): Future[RDD[Triple]] = async {
    var lang = Lang.NTRIPLES
    val pat = """(.*)[.]([^.]*)""".r
    val extension: String = input match {
      case pat(fn, ext) => ext
    }
    if (extension == "nq" || extension == "nquads") {
      lang = Lang.NQ
    } else if (extension == "rdf") {
      lang = Lang.RDFXML
    }
    val rdd: RDD[Triple] = SPARK.rdf(lang)(input)
    rdd
  }

}
