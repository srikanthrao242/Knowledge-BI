package com.knowledge.ui.controllers

import com.knowledge.server.database.AllegroGraph.AG
import scalafx.scene.control.TextArea
import scalafx.scene.layout.{HBox, VBox}
import scalafxml.core.macros.sfxml

@sfxml
class SparqlQueryController(private var vbox:VBox,
                            private var queryArea:TextArea,
                            private var hbox:HBox) {

  def executeQueryCreateTable(): Unit ={
    val ag = new AG("catalyst_ds_16","catalyst_ds_16")
    val queryString: String = queryArea.getText
    ag.sparql(queryString,true,false)
  }

  def executeQueryCreateGraph(): Unit ={
    val ag = new AG("catalyst_ds_16","catalyst_ds_16")
    val queryString: String = queryArea.getText
    ag.sparql(queryString,false,true)
  }

  def cancelExecution(): Unit ={
    println("cancel query")
    this.hbox.getScene
  }

}
