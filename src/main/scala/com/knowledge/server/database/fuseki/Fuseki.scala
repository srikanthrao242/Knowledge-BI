package com.knowledge.server.database.fuseki

import org.apache.jena.query.{QueryExecutionFactory, ResultSet}
import org.apache.jena.rdfconnection.{RDFConnection, RDFConnectionFactory}
import scala.collection.mutable.ListBuffer

class Fuseki {
  import Fuseki._

  def getConnection(): RDFConnection = {
    SERVICE_URL = SERVICE_URL.concat(if (Destination.startsWith("/")) Destination else "/" + Destination)
    RDFConnectionFactory.connectFuseki(Destination)
  }

  def sparqlSelect(query: String, table: Boolean, graph: Boolean): ResultSet = {
    val q = QueryExecutionFactory.sparqlService(SERVICE_URL, query)
    q.execSelect()
  }

  def close(conn: RDFConnection): Unit =
    try {
      conn.close()
    } catch {
      case e: Exception =>
        println("Error closing repository connection: " + e)
        e.printStackTrace()
    }

  def closeAll(): Unit =
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
