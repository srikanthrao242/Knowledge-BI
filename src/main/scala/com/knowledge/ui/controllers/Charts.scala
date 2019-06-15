/*

 * */

package com.knowledge.ui.controllers

import com.franz.agraph.jena.{AGQueryExecutionFactory, AGQueryFactory}
import com.knowledge.server.database.AllegroGraph.AG
import com.knowledge.server.database.fuseki.Fuseki.{dataset, serviceUri}
import com.knowledge.server.util.IteratorResultSetString
import com.knowledge.ui
import com.knowledge.ui.charts.{HorizontalBar, KPie}
import com.knowledge.ui.menus.NamedGraphs
import org.apache.jena.query.{QueryExecutionFactory, ResultSet}
import scalafx.scene.control.ComboBox
import scalafx.scene.layout.{Pane, StackPane}
import scalafxml.core.macros.sfxml

import scala.collection.JavaConverters._
import scalafx.Includes._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.async.Async.{async, await}

@sfxml
class Charts(private val predicate_chart: ComboBox[String],
             private val measure_chart: ComboBox[String],
             private val chart_chart: ComboBox[String],
             private val chart_graph: ComboBox[String],
             private val chart_container: Pane) {

  val stackPane = new StackPane()
  stackPane.layoutX = 133.0
  stackPane.prefHeight = 500
  stackPane.prefWidth = 1500
  chart_container.children.addAll(stackPane)

  NamedGraphs.menu.items.asScala.foreach(mi => {
    chart_graph += mi.getText
  })

  chart_chart.getItems.addAll("Pie",
                              "HBar",
                              "Line",
                              "Area",
                              "Bubble",
                              "Scatter",
                              "Bar")
  measure_chart.getItems.addAll("sum", "avg", "min", "max")

  chart_graph
    .valueProperty()
    .onChange({
      addPredicates(chart_graph.getSelectionModel.getSelectedItem)
    })

  def sparqlAG(catalog: String, repository: String, query: String): Unit =
    async {
      val ag = new AG(catalog, repository)
      val model = ag.agModel(false).get
      try {
        val sparql = AGQueryFactory.create(query)
        val qe = AGQueryExecutionFactory.create(sparql, model)
        try {
          val v = qe.execSelect()
          addPredicateMenuItem(new IteratorResultSetString(v, "p").toList)
        } finally {
          qe.close()
        }
      } finally {
        model.close()
      }
    }

  def sparqlFuseki(
    query: String
  ): Unit = async {
    try {
      val qe =
        QueryExecutionFactory.sparqlService(serviceUri + s"/$dataset", query)
      try {
        val results: ResultSet = qe.execSelect()
        addPredicateMenuItem(new IteratorResultSetString(results, "p").toList)
      } catch {
        case e: Exception =>
          e.printStackTrace()
          None
      } finally {
        qe.close()
      }
    } catch {
      case e: Exception =>
        e.printStackTrace()
        None
    }
  }

  def addPredicates(graph: String): Unit = async {
    val query = s"select distinct ?p {graph <$graph>{?s ?p ?o}}"
    if (ui.server.equalsIgnoreCase("fuseki")) {
      sparqlFuseki(query)
    } else {
      sparqlAG(AG.CATALOG, AG.REPOSITORY, query)
    }
  }

  def addPredicateMenuItem(predicate: List[String]): Unit = {
    predicate_chart.getItems.clear()
    predicate.foreach(pre => {
      predicate_chart += pre
    })
  }

  def createCharts(): Unit = {
    val cType = chart_chart.getSelectionModel.getSelectedItem
    val pre = predicate_chart.getSelectionModel.getSelectedItem
    val measure = measure_chart.getSelectionModel.getSelectedItem
    val graph = chart_graph.getSelectionModel.getSelectedItem

    val preName =
      if (pre.contains("#")) pre.split("#").last else pre.split("/").last

    val query =
      s"select distinct ?$preName ($measure(?o) as ?$measure) {graph<$graph>{?$preName ?p ?o} filter(?p = <$pre>)} group by ?$preName"
    if (cType == "Pie") {
      stackPane.resize
      new KPie().createIn(query, stackPane)
    } else if (cType == "HBar") {
      stackPane.resize
      new HorizontalBar().createIn(query, stackPane)
    }

  }

}
