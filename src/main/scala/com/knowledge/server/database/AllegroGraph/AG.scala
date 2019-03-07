package com.knowledge.server.database.AllegroGraph

import com.franz.agraph.jena.{AGGraphMaker, AGModel, AGQueryExecutionFactory, AGQueryFactory}
import com.franz.agraph.repository.{AGRepositoryConnection, AGServer}
import com.knowledge.ui.controllers.TableCreation
import org.apache.jena.query.ResultSet

import scala.collection.mutable.ArrayBuffer

class AG(CATALOG_ID : String, REPOSITORY_ID:String) {

  import AG._

  /*
  * Creating Repository
  * */

  def repository(close:Boolean):AGGraphMaker = {
    val server = new AGServer(SERVER_URL, USERNAME, PASSWORD)
    val catalog = server.getCatalog(CATALOG_ID)
    val repository = catalog.createRepository(REPOSITORY_ID)
    repository.initialize()
    val conn = repository.getConnection
    closeBeforeExit(conn)
    val maker = new AGGraphMaker(conn)
    if(close){
      maker.close()
      conn.close()
      repository.shutDown()
      null
    }else
      maker
  }


  /*
  * Get AGMODEL
  * */

  def agModel(close:Boolean):AGModel ={
    val maker = repository(false)
    val graph = maker.getGraph
    val model = new AGModel(graph)
    if(close){
      model.close()
      graph.close()
      maker.close()
      null
    }else
      model
  }

  /*
  *
  * Sparql Query
  *
  * */

  def sparql(query:String, show : Boolean): ResultSet ={
    val model = agModel(false)
    try{
      val sparql = AGQueryFactory.create(query)
      val qe = AGQueryExecutionFactory.create(sparql,model)
      try{
        val results: ResultSet = qe.execSelect()
        if(show){
          new TableCreation().createTableOfResultSet(results)
        }
        results
      }finally {
        qe.close()
      }
    }finally {
      model.close()
    }
  }


  def close(conn: AGRepositoryConnection ): Unit ={
    try{
      conn.close()
    }catch {
      case e:Exception => println("Error closing repository connection: " + e)
                          e.printStackTrace()
    }
  }

  def closeBeforeExit(conn: AGRepositoryConnection): Unit = {
    toClose += conn
  }

  def closeAll(): Unit ={
    while(toClose.nonEmpty){
      val conn = toClose.head
      close(conn)
      toClose -= conn
    }
  }

}

object AG{
  val HOST = "localhost"
  val PORT = "10035"
  val SERVER_URL = "http://" + HOST + ":" + PORT
  val USERNAME = "**********"
  val PASSWORD = "123456"
  val toClose  = new ArrayBuffer[AGRepositoryConnection]()

}