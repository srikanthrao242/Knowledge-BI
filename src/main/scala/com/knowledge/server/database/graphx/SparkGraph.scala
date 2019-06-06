package com.knowledge.server.database.graphx

import org.apache.spark.graphx._
import org.apache.jena.graph.{Node, Triple}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql._
import org.apache.spark.sql.types.{StringType, StructField, StructType}


object SparkGraph {

  def constructGraph(triples: RDD[Triple]): Graph[Node, Node] = {

    val rs = triples.map(triple => (triple.getSubject, triple.getPredicate, triple.getObject))
    val indexedMap = (rs.map(_._1) union rs.map(_._3)).distinct.zipWithUniqueId()

    val vertices: RDD[(VertexId, Node)] = indexedMap.map(x => (x._2, x._1))
    val _nodeToId: RDD[(Node, VertexId)] = indexedMap.map(x => (x._1, x._2))

    val tuples = rs.keyBy(_._1).join(indexedMap).map({
      case (k, ((s, p, o), si)) => (o, (si, p))
    })

    val edges: RDD[Edge[Node]] = tuples.join(indexedMap).map({
      case (k, ((si, p), oi)) => Edge(si, oi, p)
    })

    Graph(vertices, edges)
  }

  def toDF(graph: Graph[Node, Node]): DataFrame = {

    val spark: SparkSession = SparkSession.builder().getOrCreate()
    val schema = StructType(
      Seq(
        StructField("subject", StringType, nullable = false),
        StructField("predicate", StringType, nullable = false),
        StructField("object", StringType, nullable = false)))
    val rowRDD = toRDD(graph).map(t => Row(t.getSubject, t.getPredicate, t.getObject))
    val df = spark.createDataFrame(rowRDD, schema)
    df.createOrReplaceTempView("TRIPLES")
    df
  }

  def toRDD(graph: Graph[Node, Node]): RDD[Triple] = graph.triplets.map { case x => Triple.create(x.srcAttr, x.attr, x.dstAttr) }

  def toDS(graph: Graph[Node, Node]): Dataset[Triple] = {
    val spark: SparkSession = SparkSession.builder().getOrCreate()
    implicit val encoder = Encoders.kryo[Triple]
    spark.createDataset[Triple](toRDD(graph))
  }

}
