/*

 * */

package com.knowledge.ui.controllers

import com.franz.agraph.jena.{AGQueryExecutionFactory, AGQueryFactory}
import com.knowledge.server.database.AllegroGraph.AG
import com.knowledge.server.database.fuseki.Fuseki.{dataset, serviceUri}
import com.knowledge.server.util.IteratorResultSetString
import com.knowledge.ui
import com.knowledge.ui.charts.{ChartsUtil, HorizontalBar, KArea, KLine, KPie}
import com.knowledge.ui.menus.NamedGraphs
import org.apache.jena.query.{QueryExecutionFactory, ResultSet}
import scalafx.scene.control.{ComboBox, ListView, SelectionMode}
import scalafx.scene.layout.{Pane, StackPane}
import scalafxml.core.macros.sfxml

import scala.collection.JavaConverters._
import scalafx.Includes._
import scalafx.application.Platform

import scala.concurrent.ExecutionContext.Implicits.global
import scala.async.Async.async

@sfxml
class Charts(private val predicate_chart: ListView[String],
             private val measure_chart: ListView[String],
             private val chart_chart: ComboBox[String],
             private val chart_graph: ComboBox[String],
             private val chart_container: Pane) {

  predicate_chart.getSelectionModel.setSelectionMode(SelectionMode.Multiple)
  measure_chart.getSelectionModel.setSelectionMode(SelectionMode.Multiple)
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
      addPredicates(chart_graph.getSelectionModel.getSelectedItem,
                    addPredicateMenuItem)
    })

  chart_chart
    .valueProperty()
    .onChange({
      if (chart_chart.getSelectionModel.getSelectedItem == "Line" ||
          chart_chart.getSelectionModel.getSelectedItem == "Area") {
        addPredicates(chart_graph.getSelectionModel.getSelectedItem,
                      addMeasureMenuItem)
      }
    })
  def addPredicates(graph: String, f: List[String] => Unit): Unit = async {
    val query = s"select distinct ?p {graph <$graph>{?s ?p ?o}}"
    if (ui.server.equalsIgnoreCase("fuseki")) {
      sparqlFuseki(query, f)
    } else {
      sparqlAG(f, AG.CATALOG, AG.REPOSITORY, query)
    }
  }

  def addPredicateMenuItem(predicate: List[String]): Unit = Platform.runLater {
    predicate_chart.getItems.clear()
    predicate.foreach(pre => {
      predicate_chart.getItems += pre
    })
  }

  def addMeasureMenuItem(predicate: List[String]): Unit = Platform.runLater {
    measure_chart.getItems.clear()
    predicate.foreach(pre => {
      measure_chart.getItems += pre
    })
  }

  def sparqlAG(f: List[String] => Unit,
               catalog: String,
               repository: String,
               query: String): Unit =
    async {
      val ag = new AG(catalog, repository)
      val model = ag.agModel(false).get
      try {
        val sparql = AGQueryFactory.create(query)
        val qe = AGQueryExecutionFactory.create(sparql, model)
        try {
          val v = qe.execSelect()
          f(new IteratorResultSetString(v, "p").toList)
        } finally {
          qe.close()
        }
      } finally {
        model.close()
      }
    }

  def sparqlFuseki(
    query: String,
    f: List[String] => Unit
  ): Unit = async {
    try {
      val qe =
        QueryExecutionFactory.sparqlService(serviceUri + s"/$dataset", query)
      try {
        val results: ResultSet = qe.execSelect()
        f(new IteratorResultSetString(results, "p").toList)
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

  def createCharts(): Unit = {
    val cType = chart_chart.getSelectionModel.getSelectedItem
    val pre = predicate_chart.getSelectionModel.getSelectedItems.toList
    val measure = measure_chart.getSelectionModel.getSelectedItems.toList
    val graph = chart_graph.getSelectionModel.getSelectedItem

    val preName = ChartsUtil.nameFromURl(pre.head)
    val query =
      s"select distinct ?$preName (${measure.head}(?o) as ?${measure.head}) {graph<$graph>{?$preName ?p ?o} filter(?p = <${pre.head}>)} group by ?$preName"
    cType match {
      case "Pie" =>
        KPie(measure, stackPane, query, preName) createIn ()
      case "HBar" =>
        HorizontalBar(measure, stackPane, query, preName).createIn()
      case "Line" =>
        val meaName = ChartsUtil.nameFromURl(measure.head)
        val q =
          s"select distinct ?$preName ?$meaName {graph<$graph>{?s <${pre.head}> ?$preName ; <${measure.head}> ?$meaName}}"
        KLine(measure, stackPane, q, preName).createIn()
      case "Area" =>
        val q: String =
          ChartsUtil.createMultiMeasQuery(graph, pre.head, measure)
        KArea(measure, stackPane, q, preName) createIn ()
    }
  }

}
