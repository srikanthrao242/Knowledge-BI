package com.knowledge.ui.controllers

import com.knowledge.server.database.AllegroGraph.AG
import scalafx.scene.control.TextArea
import scalafx.scene.layout.{HBox, VBox}
import com.knowledge.server.database.GrpahQuery
import scalafxml.core.macros.sfxml

@sfxml
class SparqlQueryController(private var vbox:VBox,
                            private var queryArea:TextArea,
                            private var hbox:HBox) {

  def executeQuery(): Unit ={
    println("execution of query")
    /*val gq = new GrpahQuery()
    gq.createConfigAndRun(queryArea.getText)*/

    val ag = new AG("catalyst_ds_16","catalyst_ds_16")
    ag.sparql(queryArea.getText, true)

  }

  def cancelExecution(): Unit ={
    println("cancel query")
  }

}
