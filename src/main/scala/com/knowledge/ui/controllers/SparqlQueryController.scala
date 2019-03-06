package com.knowledge.ui.controllers

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
    val gq = new GrpahQuery()
    gq.createConfigAndRun(queryArea.getText)
  }

  def cancelExecution(): Unit ={
    println("cancel query")
  }

}
