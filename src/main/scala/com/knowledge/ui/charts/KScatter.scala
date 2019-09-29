/**/
package com.knowledge.ui.charts

import com.knowledge.server.util.IteratorResultSetQuerySolution
import org.apache.jena.query.{QuerySolution, ResultSet}
import scalafx.application.Platform
import scalafx.collections.ObservableBuffer
import scalafx.scene.chart.{NumberAxis, ScatterChart, XYChart}
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.layout.StackPane

trait KScatter extends Charts {
  override def createUI(result: ResultSet): Unit = {
    val xAxis = new NumberAxis()
    xAxis.label = predicate
    val yAxis = new NumberAxis()
    val scatter = ScatterChart(xAxis, yAxis)
    val qs = new IteratorResultSetQuerySolution(result).toList
    if (qs.nonEmpty) {
      val series = ObservableBuffer(measure.map(mes => {
        val data = ObservableBuffer(qs.map { v: QuerySolution =>
          val (measure1, measure2) = ChartsUtil.getMesNMes(v, mes, predicate)
          XYChart.Data(measure1.asInstanceOf[Number],
                       measure2.asInstanceOf[Number])
        })
        XYChart.Series[Number, Number](mes, data)
      }))
      scatter.getData.addAll(series)
      Platform.runLater {
        pane.getChildren.addAll(scatter)
      }
    } else {
      new Alert(AlertType.Information, "No Data found").showAndWait()
    }
  }
}

object KScatter {
  def apply(_measure: List[String],
            _pane: StackPane,
            _query: String,
            _predicate: String): KScatter =
    new KScatter {
      override val measure: List[String] = _measure
      override val pane: StackPane = _pane
      override val query: String = _query
      override val predicate: String = _predicate
    }
}
