package com.knowledge.server.database.fuseki

import com.knowledge.server.database.GraphServers
import com.knowledge.ui.controllers.TableCreation
import com.knowledge.ui.prefuse.GraphView
import org.apache.jena.query.{QueryExecutionFactory, QueryFactory, ReadWrite, ResultSet}
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdfconnection.{RDFConnection, RDFConnectionFactory}
import org.apache.jena.tdb.TDBFactory

import scala.collection.mutable.ListBuffer

class Fuseki extends GraphServers {
  import Fuseki._

  private def getConnection: RDFConnection = {
    SERVICE_URL = SERVICE_URL.concat(if (Destination.startsWith("/")) Destination else "/" + Destination)
    RDFConnectionFactory.connectFuseki(Destination)
  }

  override def upload(path: String): Unit = {
    val conn = getConnection
    conn.load(path)
  }

  override def sparql(query: String, table: Boolean, graph: Boolean): ResultSet = {
    val model = getModel
    try {
      val sparql = QueryFactory.create(query)
      val qe = QueryExecutionFactory.create(sparql, model)
      try {
        val results: ResultSet = qe.execSelect()
        if (table) new TableCreation().createTableOfResultSet(results)
        if (graph) new GraphView().createGraph(results, query)
        results
      } catch {
        case e: Exception =>
          e.printStackTrace()
          null
      } finally {
        qe.close()
      }
    } catch {
      case e: Exception =>
        e.printStackTrace()
        null
    } finally {
      model.close()
    }
  }

  private def getModel: Model = {
    val directory = s"MyDatabases/$Destination"
    val dataset = TDBFactory.createDataset(directory)
    dataset.begin(ReadWrite.READ)
    val model = dataset.getDefaultModel
    dataset.end()
    model
  }

  private def close(conn: RDFConnection): Unit =
    try {
      conn.close()
    } catch {
      case e: Exception =>
        println("Error closing repository connection: " + e)
        e.printStackTrace()
    }

  private def closeAll(): Unit =
    while (toClose.nonEmpty) {
      val conn = toClose.head
      close(conn)
      toClose -= conn
    }

}

object Fuseki {
  var HOST = "localhost"
  var PORT = "3030"
  var SERVICE_URL: String = "http://" + HOST + ":" + PORT
  var Destination = "/"
  val toClose = new ListBuffer[RDFConnection]()
}
