package com.knowledge.ui.controllers

import com.knowledge.server.database.AllegroGraph.AG
import com.knowledge.ui.prefuse.GraphView
import org.eclipse.rdf4j.query.QueryLanguage
import org.eclipse.rdf4j.query.algebra.StatementPattern
import org.eclipse.rdf4j.query.algebra.helpers.StatementPatternCollector
import org.eclipse.rdf4j.query.parser.QueryParserUtil
import org.eclipse.rdf4j.query.parser.sparql.SPARQLParserFactory
import scalafx.application.Platform
import scalafx.scene.control.TextArea
import scalafx.scene.layout.{HBox, VBox}
//import com.knowledge.server.database.GrpahQuery
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
