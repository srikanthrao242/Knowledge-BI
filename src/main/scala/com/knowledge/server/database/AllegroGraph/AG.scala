/*

 * */
package com.knowledge.server.database.AllegroGraph

import scala.async.Async.async
import scala.concurrent.ExecutionContext.Implicits.global
import com.franz.agraph.jena.{AGGraphMaker, AGModel, AGQueryExecutionFactory, AGQueryFactory}
import com.franz.agraph.repository.{AGRepositoryConnection, AGServer}
import com.knowledge.server.database.GraphServers
import com.knowledge.ui.controllers.TableCreation
import com.knowledge.ui.prefuse.GraphView
import org.apache.jena.query.ResultSet

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future

class AG(CATALOG_ID: String, REPOSITORY_ID: String) extends GraphServers {

  import AG._

  CATALOG = CATALOG_ID
  REPOSITORY = REPOSITORY_ID

  /*
   * Creating Repository
   * */

  private def repository(close: Boolean): Option[AGGraphMaker] = {
    val server = new AGServer(SERVER_URL, USERNAME, PASSWORD)
    val catalog = server.getCatalog(CATALOG_ID)
    val repository = catalog.createRepository(REPOSITORY_ID)
    repository.initialize()
    val conn = repository.getConnection
    closeBeforeExit(conn)
    val maker = new AGGraphMaker(conn)
    if (close) {
      maker.close()
      conn.close()
      repository.shutDown()
      None
    } else {
      Some(maker)
    }
  }

  /*
   * Get AGMODEL
   * */

  def agModel(close: Boolean): Option[AGModel] = {
    val maker = repository(false).get
    val graph = maker.getGraph
    val model = new AGModel(graph)
    if (close) {
      model.close()
      graph.close()
      maker.close()
      None
    } else {
      Some(model)
    }
  }

  /*
   *
   * Sparql Query
   *
   * */

  override def sparql(
      query: String,
      table: Boolean,
      graph: Boolean
    ): Option[ResultSet] = {
    val model = agModel(false).get
    try {
      val sparql = AGQueryFactory.create(query)
      val qe = AGQueryExecutionFactory.create(sparql, model)
      try {
        val results: ResultSet = qe.execSelect()
        // val results = ResultSetFactory.copyResults(qe.execSelect())
        if (table) new TableCreation().createTableOfResultSet(results)
        if (graph) new GraphView().createGraph(results, query)
        Some(results)
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
    } finally {
      model.close()
    }
  }

  private def close(conn: AGRepositoryConnection): Unit =
    try {
      conn.close()
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }

  private def closeBeforeExit(conn: AGRepositoryConnection): Unit =
    toClose += conn

  private def closeAll(): Unit =
    while (toClose.nonEmpty) {
      val conn = toClose.head
      close(conn)
      toClose -= conn
    }

  override def upload(graphName: String, path: String): Unit = {}
}

object AG {
  var HOST = "localhost"
  var PORT = "10035"
  val SERVER_URL = "http://" + HOST + ":" + PORT
  var USERNAME = "*****"
  var PASSWORD = "123456"
  val toClose = new ArrayBuffer[AGRepositoryConnection]()
  var CATALOG = "system"
  var REPOSITORY = ""

  def listCatalogs: Future[Array[String]] = async {
    val server = new AGServer(SERVER_URL, USERNAME, PASSWORD)
    import scala.collection.JavaConverters._
    try {
      server.listCatalogs().asScala.toArray
    } catch {
      case e: Exception => Array[String]()
    }
  }

  def listRepositories(catalog: String): Future[Array[String]] = async {
    val server = new AGServer(SERVER_URL, USERNAME, PASSWORD)
    import scala.collection.JavaConverters._
    val catalog_ag = server.getCatalog(catalog)
    if (catalog_ag != null) {
      catalog_ag.listRepositories().asScala.toArray
    } else {
      Array[String]()
    }
  }

}
