/*

 * */
package com.knowledge.ui.charts
import com.knowledge.server.database.AllegroGraph.AG
import com.knowledge.server.util.IteratorResultSetQuerySolution
import com.knowledge.ui
import org.apache.jena.query.{QuerySolution, ResultSet}
import scalafx.collections.ObservableBuffer
import scalafx.scene.chart._
import scalafx.scene.layout.StackPane

class HorizontalBar extends Charts {

  override def createIn(query: String, pane: StackPane): Unit =
    if (ui.server.equalsIgnoreCase("fuseki")) {
      sparqlFuseki(query, pane)
    } else {
      sparqlAG(AG.CATALOG, AG.REPOSITORY, query, pane)
    }

  override def createUI(result: ResultSet, pane: StackPane): Unit = {
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

    val seriese = new XYChart.Series[Number, String] {
      data = ObservableBuffer(qs.map { v: QuerySolution =>
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
        XYChart.Data(measure.asInstanceOf[Number], predicate)
      })
    }
    val chart = new BarChart(xAxis, yAxis)
    chart.barGap = 5
    chart.categoryGap = 12
    chart.title = "KnowledgeBI"
    chart.data = seriese

  }
}
