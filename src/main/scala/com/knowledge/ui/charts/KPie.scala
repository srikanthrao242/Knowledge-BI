package com.knowledge.ui.charts

import com.franz.agraph.jena.{AGQueryExecutionFactory, AGQueryFactory}
import com.knowledge.server.database.AllegroGraph.AG
import com.knowledge.server.util.IteratorResultSetQuerySolution
import org.apache.jena.query.{QuerySolution, ResultSet}
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Side
import scalafx.scene.chart.PieChart
import scalafx.scene.layout.StackPane

import scala.concurrent.ExecutionContext.Implicits.global
import scala.async.Async.async

class KPie {

  def sparql(catalog: String, repository: String, query: String, pane: StackPane): Unit = async {
    val ag = new AG(catalog, repository)
    val model = ag.agModel(false)
    try {
      val sparql = AGQueryFactory.create(query)
      val qe = AGQueryExecutionFactory.create(sparql, model)
      try {
        val ib: ResultSet = qe.execSelect()
        createPieUI(ib, pane)
      } finally {
        qe.close()
      }
    } finally {
      model.close()
    }
  }

  def createPie(query: String, pane: StackPane): Unit =
    sparql(AG.CATALOG, AG.REPOSITORY, query, pane)

  def createPieUI(result: ResultSet, pane: StackPane): Unit = {
    val qs = new IteratorResultSetQuerySolution(result).toList
    val pieChartData = ObservableBuffer(qs.map { v: QuerySolution =>
      PieChart.Data(v.get("s").toString, v.getLiteral("measure").getDouble)
    })
    val chart = new PieChart(pieChartData)
    chart.setLabelLineLength(10)
    chart.setLegendSide(Side.Left)
    chart.setPrefHeight(1000)
    chart.setPrefWidth(1000)
    chart.autosize()
    pane.getChildren.addAll(chart)
  }

}
