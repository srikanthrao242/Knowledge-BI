/**/
package com.knowledge.ui.charts
import com.knowledge.server.util.IteratorResultSetQuerySolution
import org.apache.jena.query.{QuerySolution, ResultSet}
import scalafx.application.Platform
import scalafx.collections.ObservableBuffer
import scalafx.scene.chart.{LineChart, NumberAxis, XYChart}
import scalafx.scene.layout.StackPane

trait KLine extends Charts {

  override def createUI(result: ResultSet): Unit = {
    val xAxis = new NumberAxis()
    xAxis.label = predicate
    val yAxis = new NumberAxis()
    val lineChart = LineChart(xAxis, yAxis)
    lineChart.title = "Knowledge-BI"
    val qs = new IteratorResultSetQuerySolution(result).toList
    val data = ObservableBuffer(qs.map { v: QuerySolution =>
      val (measure1, measure2) =
        ChartsUtil.getMesNMes(v, measure.head, predicate)
      XYChart.Data(measure1.asInstanceOf[Number], measure2.asInstanceOf[Number])
    })
    val series = XYChart.Series[Number, Number]("Knowledge-BI", data)
    lineChart.getData.add(series)
    Platform.runLater {
      pane.getChildren.addAll(lineChart)
    }
  }
}
object KLine {
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
