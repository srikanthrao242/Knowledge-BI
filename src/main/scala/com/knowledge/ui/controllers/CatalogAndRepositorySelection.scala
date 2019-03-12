package com.knowledge.ui.controllers

import com.knowledge.server.database.AllegroGraph.AG
import com.knowledge.server.database.entities.KAlert
import com.knowledge.ui.GraphMenu
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

  def getNamedGraphs(): Unit ={



  }

  def exitForm(): Unit ={

  }


}
