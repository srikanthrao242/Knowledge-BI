/**/
package com.knowledge.ui.charts

import com.knowledge.server.util.IteratorResultSetQuerySolution
import org.apache.jena.query.{QuerySolution, ResultSet}
import org.apache.jena.rdf.model.Literal
import scalafx.scene.chart.{CategoryAxis, NumberAxis}

import scala.util.Try

object ChartsUtil {
  def getXnYAxis(
    result: ResultSet
  ): (NumberAxis, CategoryAxis, List[QuerySolution]) = {
    val qs = new IteratorResultSetQuerySolution(result).toList
    import scala.collection.JavaConverters._
    import scalafx.Includes._
    val variables = qs.head.varNames().asScala.toList
    val yAxis: CategoryAxis = new CategoryAxis {
      label = variables.last
    }
    val xAxis: NumberAxis = new NumberAxis {
      label = variables.head
      tickLabelFormatter = NumberAxis.DefaultFormatter(this, "", "")
    }
    (xAxis, yAxis, qs)
  }
  def getMesNPre(v: QuerySolution,
                 mes: String,
                 preN: String): (Double, String) = {
    val measure = Try {
      v.getLiteral(mes).getDouble
    } getOrElse (0.0)
    val pre = v.get(preN)
    val predicate = if (pre.isURIResource) {
      if (pre.asResource().getURI.contains("#")) {
        pre.asResource().getURI.split("#").last
      } else {
        pre.asResource().getURI.split("/").last
      }
    } else {
      v.getLiteral(preN).getString
    }
    (measure, predicate)
  }

  def getMesNMes(v: QuerySolution,
                 mes: String,
                 preN: String): (Double, Double) = {
    val measure1 = Try {
      v.getLiteral(mes).getDouble
    } getOrElse (0.0)
    val measure2 = Try {
      v.getLiteral(preN).getDouble
    } getOrElse (0.0)
    (measure1, measure2)
  }

  def nameFromURl(str: String): String =
    if (str.contains("#")) {
      str.split("#").last
    } else {
      str.split("/").last
    }

  def createMultiMeasQuery(graph: String,
                           pre: String,
                           measures: List[String]): String = {
    val (select, body) = measures.foldLeft(("select ", "?s"))((acc, v) => {
      val name = nameFromURl(v)
      (s"${acc._1} ?$name", s"${acc._2} <$v> ?$name ;\n")
    })
    s"$select ?${nameFromURl(pre)}{graph<$graph>{$body <$pre> ?${nameFromURl(pre)}}}"
  }
}
