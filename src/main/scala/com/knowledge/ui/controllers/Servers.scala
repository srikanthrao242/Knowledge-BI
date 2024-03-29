/*

 * */
package com.knowledge.ui.controllers

import com.franz.agraph.jena.{AGQueryExecutionFactory, AGQueryFactory}
import com.knowledge.server.database.AllegroGraph.AG
import com.knowledge.server.database.entities.KAlert
import com.knowledge.server.database.fuseki.Fuseki
import com.knowledge.server.database.fuseki.Fuseki.{dataset, serviceUri}
import com.knowledge.server.util.IteratorResultSetString
import com.knowledge.ui
import com.knowledge.ui.GraphMenu
import com.knowledge.ui.menus.NamedGraphs
import org.apache.jena.query.{QueryExecutionFactory, ResultSet}
import scalafx.application.Platform
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{ChoiceBox, ListView, ProgressIndicator, TextField}
import scalafxml.core.macros.sfxml
import com.knowledge.ui._

import scala.async.Async.async

@sfxml
class Servers(private var serverIP: TextField,
              private var serverPort: TextField,
              private var serverUser: TextField,
              private var serverPassword: TextField,
              private var catalogView: ListView[String],
              private var RepositoryView: ListView[String],
              private var fusekiServerUri: TextField,
              private var fusekiModelName: TextField,
              private var fusekiAccessModel: ChoiceBox[String],
              private var fusekiPath: TextField,
              private var fusekiDs: TextField) {

  import scala.concurrent.ExecutionContext.Implicits.global

  def checkServerAndGetCatalogs(): Unit = {
    AG.HOST = serverIP.text.get()
    AG.PORT = serverPort.text.get()
    AG.USERNAME = serverUser.text.get()
    AG.PASSWORD = serverPassword.text.get()
    server = "AG"
    val pb = new ProgressIndicator()
    pb.visible = false
    AG.listCatalogs.onComplete(v => {
      v.get match {
        case catalogs: Array[String] =>
          Platform.runLater(catalogView.items = ObservableBuffer(catalogs: _*))
      }
    })

  }

  def getRepositories(): Unit = {
    val catalog = catalogView.getSelectionModel.getSelectedItems.get(0)
    AG.listRepositories(catalog)
      .onComplete(v => {
        v.get match {
          case repositories: Array[String] =>
            if (repositories.isEmpty) {
              KAlert("No Repositories exists...", GraphMenu.stage)
            } else {
              Platform.runLater(
                RepositoryView.items = ObservableBuffer(repositories: _*)
              )
            }
        }
      })
  }

  def sparqlFuseki(
    query: String
  ): Unit = async {
    try {
      val qe =
        QueryExecutionFactory.sparqlService(serviceUri + s"/$dataset", query)
      try {
        val results: ResultSet = qe.execSelect()
        val ib: List[String] = new IteratorResultSetString(results, "g").toList
        NamedGraphs.addGraphs(ib)
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

  def sparqlAG(catalog: String, repository: String, query: String): Unit = {
    val ag = new AG(catalog, repository)
    val model = ag.agModel(false).get
    try {
      val sparql = AGQueryFactory.create(query)
      val qe = AGQueryExecutionFactory.create(sparql, model)
      try {
        val results: ResultSet = qe.execSelect()
        val ib: List[String] = new IteratorResultSetString(results, "g").toList
        NamedGraphs.addGraphs(ib)
      } finally {
        qe.close()
      }
    } finally {
      model.close()
    }
  }

  def getNamedGraphs(): Unit = {
    val catalogs = catalogView.getSelectionModel.getSelectedItems
    val repositories = RepositoryView.getSelectionModel.getSelectedItems
    val query = "SELECT DISTINCT ?g{GRAPH ?g{?s ?p ?o}}"
    if (ui.server.equalsIgnoreCase("fuseki")) {
      sparqlFuseki(query)
    } else if (catalogs.size() > 0 && repositories.size() > 0) {
      sparqlAG(AG.CATALOG, AG.REPOSITORY, query)
    } else {
      KAlert("Select catalog and repository.... ", GraphMenu.stage)
    }
  }

  def saveFusekiConf(): Unit = {
    Fuseki.serviceUri = fusekiServerUri.text.get()
    Fuseki.modelName = fusekiModelName.text.get()
    Fuseki.path = fusekiPath.text.get()
    Fuseki.dataAccessMode = fusekiAccessModel.getSelectionModel.getSelectedItem
    Fuseki.dataset = fusekiDs.text.get()
    server = "Fuseki"
    getNamedGraphs()
  }

  def exitFusekiForm(): Unit = {}

  def exitForm(): Unit = {}

}
