/*

 * */

package com.knowledge.ui.controllers

import com.franz.agraph.jena.{AGQueryExecutionFactory, AGQueryFactory}
import com.knowledge.server.database.AllegroGraph.AG
import com.knowledge.server.util.IteratorResultSetString
import com.knowledge.ui.charts.KPie
import com.knowledge.ui.menus.NamedGraphs
import scalafx.scene.control.ComboBox
import scalafx.scene.layout.{Pane, StackPane}
import scalafxml.core.macros.sfxml

import scala.collection.JavaConverters._
import scalafx.Includes._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.async.Async.{async, await}

@sfxml
class Charts(
    private val predicate_chart: ComboBox[String],
    private val measure_chart: ComboBox[String],
    private val chart_chart: ComboBox[String],
    private val chart_graph: ComboBox[String],
    private val chart_container: Pane) {

  val stackPane = new StackPane()
  // stackPane.layoutX = 133.0
  stackPane.prefHeight = 500
  stackPane.prefWidth = 1500
  // stackPane.id = "chart_graphs"
  chart_container.children.addAll(stackPane)

  NamedGraphs.menu.items.asScala.foreach(mi => {
    chart_graph += mi.getText
  })

  chart_chart.getItems.addAll("Pie", "Line", "Area", "Bubble", "Scatter", "Bar")
  measure_chart.getItems.addAll("sum", "avg", "min", "max")

  chart_graph
    .valueProperty()
    .onChange({
      addPredicates(chart_graph.getSelectionModel.getSelectedItem)
    })

  def sparql(catalog: String, repository: String, query: String): Unit = async {
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

  def addPredicates(graph: String): Unit = async {
    val query = s"select distinct ?p {graph <$graph>{?s ?p ?o}}"
    sparql(AG.CATALOG, AG.REPOSITORY, query)
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
    val query =
      s"select distinct ?s ($measure(?o) as ?measure) {graph<$graph>{?s ?p ?o} filter(?p = <$pre>)} group by ?s"
    if (cType == "Pie") {
      new KPie().createPie(query, stackPane)
    }

  }

}
