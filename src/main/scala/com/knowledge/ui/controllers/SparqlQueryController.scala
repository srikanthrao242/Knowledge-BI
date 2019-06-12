/*
 */

package com.knowledge.ui.controllers

import com.knowledge.server.database.AllegroGraph.AG
import com.knowledge.server.database.GraphServers
import com.knowledge.server.database.fuseki.Fuseki
import scalafx.scene.control.TextArea
import scalafx.scene.layout.{HBox, VBox}
import scalafxml.core.macros.sfxml
import com.knowledge.ui._

@sfxml
class SparqlQueryController(
    private var vbox: VBox,
    private var queryArea: TextArea,
    private var hbox: HBox) {

  def gerServerObject(): GraphServers =
    if (server == "AG") {
      new AG("catalyst_ds_16", "catalyst_ds_16")
    } else {
      new Fuseki
    }

  def executeQueryCreateTable(): Unit = {
    val ag = gerServerObject()
    val queryString: String = queryArea.getText
    ag.sparql(queryString, true, false)
  }

  def executeQueryCreateGraph(): Unit = {
    val ag = gerServerObject()
    val queryString: String = queryArea.getText
    ag.sparql(queryString, false, true)
  }

  def cancelExecution(): Unit = {
    println("cancel query")
    this.hbox.getScene
  }

}
