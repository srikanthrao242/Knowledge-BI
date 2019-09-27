/*

 * */
package com.knowledge.ui.charts
import com.knowledge.server.database.AllegroGraph.AG
import com.knowledge.server.util.IteratorResultSetQuerySolution
import com.knowledge.ui
import org.apache.jena.query.{QuerySolution, ResultSet}
import scalafx.application.Platform
import scalafx.collections.ObservableBuffer
import scalafx.scene.chart._
import scalafx.scene.layout.StackPane

class HorizontalBar extends Charts {

  override def createIn(query: String,
                        pane: StackPane,
                        predicate: String,
                        measure: String): Unit =
    if (ui.server.equalsIgnoreCase("fuseki")) {
      sparqlFuseki(query, pane, predicate, measure)
    } else {
      sparqlAG(AG.CATALOG, AG.REPOSITORY, query, pane, predicate, measure)
    }

  override def createUI(result: ResultSet,
                        pane: StackPane,
                        preN: String,
                        mes: String): Unit = {
    val qs = new IteratorResultSetQuerySolution(result).toList
    import scala.collection.JavaConverters._
    import scalafx.Includes._
    val variables = qs.head.varNames().asScala.toList
    val yAxis = new CategoryAxis {
      label = variables.last
    }
    val xAxis = new NumberAxis {
      label = variables.head
      tickLabelFormatter = NumberAxis.DefaultFormatter(this, "", "")
    }

    val series = new XYChart.Series[Number, String] {
      data = ObservableBuffer(qs.map { v: QuerySolution =>
        val measure = try {
          v.getLiteral(mes).getDouble
        } catch {
          case e: Exception => 0.0
        }
        val pre = v.getResource(preN).getURI
        val predicate = if (pre.contains("#")) {
          pre.split("#").last
        } else {
          pre.split("/").last
        }
        XYChart.Data(measure.asInstanceOf[Number], predicate)
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
  }
}
