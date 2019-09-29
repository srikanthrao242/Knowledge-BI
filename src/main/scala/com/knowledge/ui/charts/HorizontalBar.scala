/*

 * */
package com.knowledge.ui.charts
import org.apache.jena.query.{QuerySolution, ResultSet}
import scalafx.application.Platform
import scalafx.collections.ObservableBuffer
import scalafx.scene.chart._
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.layout.StackPane

trait HorizontalBar extends Charts {
  override def createUI(result: ResultSet): Unit = {
    val (xAxis, yAxis, qs) = ChartsUtil.getXnYAxis(result)
    if (qs.nonEmpty) {
      val series = new XYChart.Series[Number, String] {
        data = ObservableBuffer(qs.map { v: QuerySolution =>
          val (mes, pre) = ChartsUtil.getMesNPre(v, measure.head, predicate)
          XYChart.Data(mes.asInstanceOf[Number], pre)
        })
      }
      Platform.runLater {
        val chart = new BarChart(xAxis, yAxis)
        chart.barGap = 5
        chart.categoryGap = 12
        chart.title = "KnowledgeBI"
        chart.data = series
        chart.autosize()
        pane.getChildren.addAll(chart)
      }
    } else {
      new Alert(AlertType.Information, "No Data found").showAndWait()
    }
  }
}

object HorizontalBar {
  def apply(_measure: List[String],
            _pane: StackPane,
            _query: String,
            _predicate: String): HorizontalBar =
    new HorizontalBar {
      override val measure: List[String] = _measure
      override val pane: StackPane = _pane
      override val query: String = _query
      override val predicate: String = _predicate
    }
}
