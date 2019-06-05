package com.knowledge.ui.menus

import com.franz.agraph.jena.{AGQueryExecutionFactory, AGQueryFactory}
import com.knowledge.server.database.AllegroGraph.AG
import com.knowledge.server.util.IteratorResultSetTriples
import com.knowledge.ui.GraphMenu
import org.apache.jena.query.ResultSet
import scalafx.scene.control.{Menu, MenuItem, TextInputDialog}
import org.apache.jena.graph.Triple

import scala.util.Try

object NamedGraphs {

  import scalafx.Includes._
  val menu: Menu = new Menu("NamedGraph")

  def addMenus(graph: String): Unit = {
    val item = new MenuItem(graph)
    menu.items.addAll(item)
    item.onAction = handle({
      val limit = limitPopup()
      createTable(graph, limit)
      getMeasures(graph, limit)
    })
  }

  def sparql(catalog: String, repository: String, query: String): Unit = {
    val ag = new AG(catalog, repository)
    val model = ag.agModel(false)
    try {
      val sparql = AGQueryFactory.create(query)
      val qe = AGQueryExecutionFactory.create(sparql, model)
      try {
        val results: ResultSet = qe.execSelect()
        val ib: List[Triple] = new IteratorResultSetTriples(results).toList
        // new Measures(Left(ib))
      } finally {
        qe.close()
      }
    } finally {
      model.close()
    }
  }

  def getMeasures(graph: String, limit: Int): Unit = {
    val sparql = s"SELECT ?s ?p ?o {graph<$graph>{?s ?p ?o}} limit $limit"
    this.sparql(AG.CATALOG, AG.REPOSITORY, sparql)
  }

  def createTable(graph: String, limit: Int): Unit = {
    val sparql = s"SELECT ?s ?p ?o {graph<$graph>{?s ?p ?o}} limit $limit"
    val ag = new AG(AG.CATALOG, AG.REPOSITORY)
    ag.sparql(sparql, true, false)
  }

  def limitPopup(): Int = {
    val dialog = new TextInputDialog(defaultValue = "0") {
      initOwner(GraphMenu.stage)
      title = "KNOWLEDGE-BI"
      headerText = ""
      contentText = "Please enter limit :"
    }
    val result = dialog.showAndWait()
    result match {
      case Some(limit) =>
        Try {
          limit.toInt
        }.getOrElse(limitPopup())
      case None => limitPopup()
    }
  }

  def addGraphs(graphs: List[String]): Unit = {
    graphs.foreach(addMenus)
    GraphMenu.menuBar.menus.add(menu)
  }

}
