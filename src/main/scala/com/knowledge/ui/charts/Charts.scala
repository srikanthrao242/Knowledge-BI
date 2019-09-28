/*

 * */
package com.knowledge.ui.charts

import com.franz.agraph.jena.{AGQueryExecution, AGQueryExecutionFactory, AGQueryFactory}
import com.knowledge.server.database.AllegroGraph.AG
import com.knowledge.server.database.fuseki.Fuseki.{dataset, serviceUri}
import com.knowledge.ui
import org.apache.jena.query.{QueryExecution, QueryExecutionFactory, ResultSet}
import scalafx.scene.layout.StackPane

import scala.concurrent.ExecutionContext.Implicits.global
import scala.async.Async.async

trait Charts {
  val query: String
  val pane: StackPane
  val measure: List[String]
  val predicate: String
  def createIn(): Unit =
    if (ui.server.equalsIgnoreCase("fuseki")) {
      sparqlFuseki()
    } else {
      sparqlAG(AG.CATALOG, AG.REPOSITORY)
    }

  def createUI(result: ResultSet): Unit

  def doUI(qe: QueryExecution): Unit =
    try {
      val ib: ResultSet = qe.execSelect()
      createUI(ib)
    } finally {
      qe.close()
    }

  def sparqlAG(
    catalog: String,
    repository: String
  ): Unit = async {
    val ag = new AG(catalog, repository)
    val model = ag.agModel(false).get
    try {
      val sparql = AGQueryFactory.create(query)
      val qe = AGQueryExecutionFactory.create(sparql, model)
      doUI(qe)
    } finally {
      model.close()
    }
  }

  def sparqlFuseki(): Unit = async {
    try {
      val qe: QueryExecution =
        QueryExecutionFactory.sparqlService(serviceUri + s"/$dataset", query)
      doUI(qe)
    } catch {
      case e: Exception =>
        e.printStackTrace()
        None
    }
  }

}
