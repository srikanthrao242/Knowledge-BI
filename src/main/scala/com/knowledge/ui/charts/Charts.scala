/*

 * */
package com.knowledge.ui.charts

import com.franz.agraph.jena.{AGQueryExecutionFactory, AGQueryFactory}
import com.knowledge.server.database.AllegroGraph.AG
import com.knowledge.server.database.fuseki.Fuseki.{dataset, serviceUri}
import org.apache.jena.query.{QueryExecutionFactory, ResultSet}
import scalafx.scene.layout.StackPane
import scala.concurrent.ExecutionContext.Implicits.global
import scala.async.Async.async

trait Charts {
  def createIn(query: String,
               pane: StackPane,
               predicate: String,
               measure: String): Unit
  def createUI(result: ResultSet,
               pane: StackPane,
               predicate: String,
               measure: String): Unit
  def sparqlAG(
    catalog: String,
    repository: String,
    query: String,
    pane: StackPane,
    predicate: String,
    measure: String
  ): Unit = async {
    val ag = new AG(catalog, repository)
    val model = ag.agModel(false).get
    try {
      val sparql = AGQueryFactory.create(query)
      val qe = AGQueryExecutionFactory.create(sparql, model)
      try {
        val ib: ResultSet = qe.execSelect()
        createUI(ib, pane, predicate, measure)
      } finally {
        qe.close()
      }
    } finally {
      model.close()
    }
  }

  def sparqlFuseki(
    query: String,
    pane: StackPane,
    predicate: String,
    measure: String
  ): Unit = async {
    try {
      val qe =
        QueryExecutionFactory.sparqlService(serviceUri + s"/$dataset", query)
      try {
        val results: ResultSet = qe.execSelect()
        createUI(results, pane, predicate, measure)
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

}
