package com.knowledge.server.database

import com.knowledge.server.database.entities.Config
import com.knowledge.server.sparkCore.SparkCoreModule
import com.knowledge.server.util.ReadRDF
import com.knowledge.ui.controllers.TripleClass
import net.sansa_stack.query.spark.graph.jena.{Ops, SparqlParser}
import net.sansa_stack.query.spark.graph.jena.model.{IntermediateResult, SparkExecutionModel, Config => modelConfig}
import net.sansa_stack.query.spark.graph.jena.resultOp.ResultOp
import net.sansa_stack.rdf.spark.partition.graph.algo._
import org.apache.jena.graph.Node
import org.apache.jena.query.{Query, QueryFactory}
import org.apache.jena.riot.Lang
import org.apache.jena.sparql.algebra.{Algebra, Op}
import org.apache.log4j.Logger
import org.apache.spark.graphx.Graph

import scala.concurrent.duration.Duration
import scala.io.Source
import scala.util.{Failure, Success}
import net.sansa_stack.query.spark.query._

import scala.concurrent.ExecutionContext.Implicits.global

class GrpahQuery extends SparkCoreModule{

  def main(args: Array[String]): Unit = {

    parser.parse(args, Config()) match {
      case Some(config) => run(config)
      case None =>
        println(parser.usage)
    }
  }

  def createConfigAndRun(query:String,repository:String):Unit={
    val config = Config(CreateSchemas.getDefaultCatalog+"/"+repository+"/*",
      Seq(query),true)


/*
    ReadRDF.readNtriples(System.getProperty("user.dir")+"/dataset/rdf.nt").onComplete {
      case Success(v) =>
       val rdd = v.sparql(query)
        rdd.show(false)
      case Failure(ex) => println(ex)
    }*/

    run(config)
  }

  def run(config: Config): Unit = {

    println("===========================================")
    println("| SANSA - Graph query execution     |")
    println("===========================================")

    val log = Logger.getLogger("GrpahQuery")

    // set configures for query engine model
    modelConfig.setAppName("Graph Query")
      .setInputGraphFile(config.input)
      .setInputQueryFile(config.query.head)
      .setLang(Lang.NTRIPLES)
      .setMaster("local[*]")

    // load graph
    log.info("Start to load graph")

    //SparkExecutionModel.getSession
    val session = SparkExecutionModel.getSession

    // apply graph partitioning algorithm
    val prevG = SparkExecutionModel.getGraph
    var g: Graph[Node, Node] = null
    var msg: String = null
    var numParts: Int = 0
    var numIters: Int = 0


    // Set number of partitions (if config.numParts is 0, number of partitions equals to that of previous graph)
    config.numParts match {
      case 0     => numParts = 3
      case other => numParts = other
    }

    config.numIters match {
      case 0     =>
      case other => numIters = other
    }

    var partAlgo: PartitionAlgo[Node, Node] = null

    config.algo match {
      case "SSHP" =>
        if (numIters == 0) {
          // Partition algorithm will use default number of iterations
          partAlgo = new SubjectHashPartition[Node, Node](prevG, session, numParts)
        } else {
          partAlgo = new SubjectHashPartition[Node, Node](prevG, session, numParts).setNumIterations(numIters)
        }
        msg = "Start to execute subject semantic hash partitioning"
      case "OSHP" =>
        if (numIters == 0) {
          partAlgo = new ObjectHashPartition[Node, Node](prevG, session, numParts)
        } else {
          partAlgo = new ObjectHashPartition[Node, Node](prevG, session, numParts).setNumIterations(numIters)
        }
        msg = "Start to execute object semantic hash partitioning"
      case "SOSHP" =>
        if (numIters == 0) {
          partAlgo = new SOHashPartition[Node, Node](prevG, session, numParts)
        } else {
          partAlgo = new SOHashPartition[Node, Node](prevG, session, numParts).setNumIterations(numIters)
        }
        msg = "Start to execute subject-object semantic hash partitioning"
      case "PP" =>
        if (numIters == 0) {
          partAlgo = new PathPartition[Node, Node](prevG, session, numParts)
        } else {
          partAlgo = new PathPartition[Node, Node](prevG, session, numParts).setNumIterations(numIters)
        }
        msg = "Start to execute path partitioning"
      case ""    =>
      case other => println(s"the input $other doesn't match any options, no algorithm will be applied.")
    }

    var start = 0L
    var end = 0L

    if (partAlgo != null) {
      log.info(msg)
      start = System.currentTimeMillis()
      g = partAlgo.partitionBy().cache()
      SparkExecutionModel.loadGraph(g)
      end = System.currentTimeMillis()
      log.info("Graph partitioning execution time: " + Duration(end - start, "millis").toMillis + " ms")
    }

    // query executing
    log.info("Start to execute queries")

   // config.query.foreach { path =>
      val path = config.query.head
      log.info("Query file: " + path)
      //modelConfig.setInputQueryFile(path)
      //val sp = new SparqlParser(modelConfig.getInputQueryFile)
      val op: Query = QueryFactory.create(path)
      val sp = new SparqlParser(Algebra.compile(op))
      sp.getOps.dequeue().asInstanceOf[ResultOp].execute()
      /*sp.getOps.foreach { ops =>
        val tag = ops.getTag
        log.info("Operation " + tag + " start")
        start = System.currentTimeMillis()
        ops.execute()
        end = System.currentTimeMillis()
        log.info(tag + " execution time: " + Duration(end - start, "millis").toMillis + " ms")
      }*/
   // }

    // print results to console
    if (config.print) {
      log.info("print final result")
      val results = IntermediateResult.getFinalResult.cache()
      if (results.count() >= 10) {
        log.info("Too long results(more than 10)")
      } else {
        results.collect().foreach(println(_))
      }
      results.unpersist()
    }
  }


  val parser: scopt.OptionParser[Config] = new scopt.OptionParser[Config]("Spark-Graph-Example") {

    head("SANSA-Query-Graph-Example")

    opt[String]('i', "input").required().valueName("<path>").
      action((x, c) => c.copy(input = x)).
      text("path to file that contains the data (in N-Triples format).")

    opt[Seq[String]]('q', "query").required().valueName("<query1>, <query2>...").
      action((x, c) => c.copy(query = x)).
      text("files that contain SPARQL queries.")

    opt[Boolean]('p', "print").optional().valueName("Boolean").
      action((_, c) => c.copy(print = true)).
      text("print the result to the console(maximum 10 rows), default: false.")

    opt[String]('a', "algorithm").optional().valueName("<SSHP | OSHP | SOSHP | PP>").
      action((x, c) => c.copy(algo = x)).
      text("choose one graph partitioning algorithm, default: no algorithm applied.")

    opt[Int]('n', "number of partitions").optional().valueName("<Int>")
      .action((x, c) => c.copy(numParts = x))
      .text("set the number of partitions.")

    opt[Int]('t', "number of iterations").optional().valueName("<Int>")
      .action((x, c) => c.copy(numIters = x))
      .text("set the number of iterations.")

    help("help").text("prints this usage text")
  }

}
