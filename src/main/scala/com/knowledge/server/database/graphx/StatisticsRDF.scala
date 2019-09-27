/**/
package com.knowledge.server.database.graphx

import com.knowledge.server.sparkCore.SparkCoreModule
import org.apache.jena.graph.Triple
import org.apache.spark.rdd.RDD
import org.apache.jena.vocabulary.XSD

import scala.util.Try

object StatisticsRDF extends SparkCoreModule {

  def maxPerProperty(triples: RDD[Triple]): (Triple, Int) = {
    val max_per_property_def = triples.filter(
      triple =>
        triple.getObject.isLiteral &&
        (triple.getObject.getLiteralDatatypeURI == XSD.integer.toString ||
        triple.getObject.getLiteralDatatypeURI == XSD.xint.toString ||
        triple.getObject.getLiteralDatatypeURI == XSD.xfloat.toString)
    )

    val properties_fr = max_per_property_def.map(f => (f, 1)).reduceByKey(_ + _)

    val ordered = properties_fr.takeOrdered(1)(Ordering[Int].reverse.on(_._2))
    ordered.maxBy(_._2)
  }

  def avgPerProperty(triples: RDD[Triple]): RDD[(Triple, Double)] = {
    val avg_per_property_def = triples.filter(
      triple =>
        triple.getObject.isLiteral &&
        (triple.getObject.getLiteralDatatypeURI == XSD.integer.toString ||
        triple.getObject.getLiteralDatatypeURI == XSD.xint.toString ||
        triple.getObject.getLiteralDatatypeURI == XSD.xfloat.toString)
    )

    val sumCountPair = avg_per_property_def
      .map((_, 1))
      .combineByKey(
        (x: Int) => (x.toDouble, 1),
        (pair1: (Double, Int), x: Int) => (pair1._1 + x, pair1._2 + 1),
        (pair1: (Double, Int), pair2: (Double, Int)) =>
          (pair1._1 + pair2._1, pair1._2 + pair2._2)
      )
    val average = sumCountPair.map(x => (x._1, (x._2._1 / x._2._2)))
    average
  }

  def sumPerProperty(triples: RDD[Triple]): RDD[(Triple, Double)] = {
    val avg_per_property_def = triples.filter(
      triple =>
        triple.getObject.isLiteral &&
        (triple.getObject.getLiteralDatatypeURI == XSD.integer.toString ||
        triple.getObject.getLiteralDatatypeURI == XSD.xint.toString ||
        triple.getObject.getLiteralDatatypeURI == XSD.xfloat.toString)
    )

    val sumCountPair = avg_per_property_def
      .map((_, 1))
      .combineByKey(
        (x: Int) => (x.toDouble, 1),
        (pair1: (Double, Int), x: Int) => (pair1._1 + x, pair1._2 + 1),
        (pair1: (Double, Int), pair2: (Double, Int)) =>
          (pair1._1 + pair2._1, pair1._2 + pair2._2)
      )
    val sum = sumCountPair.map(x => (x._1, x._2._1))
    sum
  }

  /*RDD[(String,Double)]*/
  def avgPerPredicate(triples: RDD[Triple]): RDD[(String, Int)] = {
    val avg_per_property_def = triples.filter(
      triple =>
        triple.getObject.isLiteral &&
        (triple.getObject.getLiteralDatatypeURI == XSD.integer.toString ||
        triple.getObject.getLiteralDatatypeURI == XSD.xint.toString ||
        triple.getObject.getLiteralDatatypeURI == XSD.xfloat.toString)
    )
    import Numeric.Implicits._
    import scala.collection.JavaConverters._
    def getSum[T <: AnyRef](x: List[T]): T =
      x match {
        case n: List[java.lang.Integer] =>
          n.asInstanceOf[List[Int]].sum.asInstanceOf
        case n: List[java.lang.Float] =>
          n.asInstanceOf[List[Float]].sum.asInstanceOf
        case n: List[java.lang.Double] =>
          n.asInstanceOf[List[Double]].sum.asInstanceOf
        case n: List[java.lang.Long] =>
          n.asInstanceOf[List[Long]].sum.asInstanceOf
      }

    import scala.language.implicitConversions
    avg_per_property_def
      .groupBy(_.getPredicate.getURI)
      .map(
        v =>
          (v._1,
           v._2.map(_.getObject.getLiteralValue.asInstanceOf[AnyVal]).toList)
      )
      .map(
        v =>
          (v._1,
           v._2.foldLeft(0)(
             (acc, v) =>
               acc + Try {
                 v.asInstanceOf
               }.getOrElse(0)
           ))
      )
  }

}
