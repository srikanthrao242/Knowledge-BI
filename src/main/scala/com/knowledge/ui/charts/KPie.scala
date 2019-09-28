/*
 */
package com.knowledge.ui.charts

import com.knowledge.server.util.IteratorResultSetQuerySolution
import org.apache.jena.query.{QuerySolution, ResultSet}
import scalafx.application.Platform
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Side
import scalafx.scene.chart.PieChart
import scalafx.scene.layout.StackPane

trait KPie extends Charts {
  override def createUI(result: ResultSet): Unit = {
    val qs = new IteratorResultSetQuerySolution(result).toList
    val pieChartData = ObservableBuffer(qs.map { v: QuerySolution =>
      val (mes, pre) = ChartsUtil.getMesNPre(v, measure.head, predicate)
      PieChart.Data(pre, mes)
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

object KPie {
  def apply(_measure: List[String],
            _pane: StackPane,
            _query: String,
            _predicate: String): KPie =
    new KPie {
      override val measure: List[String] = _measure
      override val pane: StackPane = _pane
      override val query: String = _query
      override val predicate: String = _predicate
    }
}
