/*
 */
package com.knowledge.ui.charts

import com.knowledge.server.database.AllegroGraph.AG
import com.knowledge.server.util.IteratorResultSetQuerySolution
import com.knowledge.ui
import org.apache.jena.query.{QuerySolution, ResultSet}
import scalafx.application.Platform
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Side
import scalafx.scene.chart.PieChart
import scalafx.scene.layout.StackPane

class KPie extends Charts {

  override def createIn(query: String, pane: StackPane): Unit =
    if (ui.server.equalsIgnoreCase("fuseki")) {
      sparqlFuseki(query, pane)
    } else {
      sparqlAG(AG.CATALOG, AG.REPOSITORY, query, pane)
    }

  override def createUI(result: ResultSet, pane: StackPane): Unit = {
    val qs = new IteratorResultSetQuerySolution(result).toList
    import scala.collection.JavaConverters._
    val variables = qs.head.varNames().asScala.toList
    val pieChartData = ObservableBuffer(qs.map { v: QuerySolution =>
      val measure = try {
        v.getLiteral(variables.last).getDouble
      } catch {
        case e: Exception => 0.0
      }
      val pre = v.get(variables.head).toString
      val predicate = if (pre.contains("#")) {
        pre.split("#").last
      } else {
        pre.split("/").last
      }
      PieChart.Data(predicate, measure)
    })
    Platform.runLater {
      val chart = new PieChart(pieChartData)
      chart.setLabelLineLength(10)
      chart.setLegendSide(Side.Left)
      chart.setPrefHeight(1000)
      chart.setPrefWidth(1000)
      chart.autosize()
      pane.getChildren.addAll(chart)
    }
  }

}
