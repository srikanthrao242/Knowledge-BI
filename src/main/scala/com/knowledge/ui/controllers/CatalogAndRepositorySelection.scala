package com.knowledge.ui.controllers

import com.franz.agraph.jena.{AGQueryExecutionFactory, AGQueryFactory}
import com.knowledge.server.database.AllegroGraph.AG
import com.knowledge.server.database.entities.KAlert
import com.knowledge.server.util.IteratorResultSetString
import com.knowledge.ui.GraphMenu
import com.knowledge.ui.menus.NamedGraphs
import org.apache.jena.query.ResultSet
import scalafx.application.Platform
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{ListView, ProgressIndicator, TextField}
import scalafxml.core.macros.sfxml

import scala.concurrent.ExecutionContext.Implicits.global

@sfxml
class CatalogAndRepositorySelection(private var serverIP : TextField,
                                    private var serverPort : TextField,
                                    private var serverUser : TextField,
                                    private var serverPassword : TextField,
                                    private var catalogView : ListView[String],
                                    private var RepositoryView : ListView[String]) {


  def checkServerAndGetCatalogs(): Unit ={
    AG.HOST = serverIP.text.get()
    AG.PORT = serverPort.text.get()
    AG.USERNAME = serverUser.text.get()
    AG.PASSWORD = serverPassword.text.get()
    val pb = new ProgressIndicator()
    pb.visible = false
    AG.listCatalogs.onComplete(v=>{
      v.get match {
        case catalogs:Array[String] => Platform.runLater(catalogView.items = ObservableBuffer(catalogs:_*))
      }
    })

  }

  def getRepositories(): Unit ={
    val catalog = catalogView.getSelectionModel.getSelectedItems.get(0)
    AG.listRepositories(catalog).onComplete(v=>{
      v.get match {
        case repositories:Array[String] =>
          if(repositories.isEmpty)
            KAlert("No Repositories exists...",GraphMenu.stage)
          else
            Platform.runLater(RepositoryView.items = ObservableBuffer(repositories:_*))
      }
    })
  }

  def sparql(catalog:String,repository:String,query:String): Unit ={
    val ag = new AG(catalog,repository)
    val model = ag.agModel(false)
    try{
      val sparql = AGQueryFactory.create(query)
      val qe = AGQueryExecutionFactory.create(sparql,model)
      try{
        val results: ResultSet = qe.execSelect()
        val ib: List[String] = new IteratorResultSetString(results,"g").toList
        NamedGraphs.addGraphs(ib)
      }
      finally {
        qe.close()
      }
    }
    finally {
      model.close()
    }
  }


  def getNamedGraphs(): Unit ={
    val catalogs = catalogView.getSelectionModel.getSelectedItems
    val repositories = RepositoryView.getSelectionModel.getSelectedItems
    if(catalogs.size() > 0 && repositories.size() > 0){
      val catalog = catalogs.get(0)
      val repository = repositories.get(0)
      val query = "SELECT DISTINCT ?g{GRAPH ?g{?s ?p ?o}}"
      sparql(catalog,repository,query)
    }else{
      println("No catalog and repository selected")
      KAlert("Select catalog and repository.... ",GraphMenu.stage)
    }
  }

  def exitForm(): Unit ={

  }


}
