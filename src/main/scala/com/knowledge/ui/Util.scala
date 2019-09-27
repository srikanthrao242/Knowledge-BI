/*
 */
package com.knowledge.ui

import java.net.URL

import com.franz.agraph.jena.{AGQueryExecutionFactory, AGQueryFactory}
import com.knowledge.server.database.AllegroGraph.AG
import org.apache.jena.query.ResultSet
import scalafx.scene.Scene
import scalafx.stage.Stage
import scalafxml.core.{FXMLView, NoDependencyResolver}
import scalafx.application.Platform
import scalafx.scene.control.{Menu, MenuItem}
import scalafx.scene.input.KeyCombination
import scala.async.Async.async
import scala.concurrent.Future

object Util {

  import scala.concurrent.ExecutionContext.Implicits.global
  import scalafx.Includes._

  def loadFXML(fxml: URL): Unit = {
    val root = FXMLView(fxml, NoDependencyResolver)
    val dialogStage = new Stage()
    dialogStage.title = "File Upload"
    dialogStage.scene = new Scene(root)
    dialogStage.show()
  }

  def createMenuItemAndLoad(
    menuName: String,
    fxmlPath: String,
    menu: Menu,
    accelerator: String
  ): Unit = {
    val mi = new MenuItem(menuName)
    val fxml: URL = getClass.getResource(fxmlPath)
    if (fxmlPath.nonEmpty) mi.onAction = handle {
      Platform.runLater(loadFXML(fxml))
    }
    if (accelerator.nonEmpty) {
      mi.accelerator = KeyCombination.keyCombination(accelerator)
    }
    menu.items.add(mi)
  }

  def sparql(
    catalog: String,
    repository: String,
    query: String
  ): Future[ResultSet] = async {
    val ag = new AG(catalog, repository)
    val model = ag.agModel(false).get
    try {
      val sparql = AGQueryFactory.create(query)

      val qe = AGQueryExecutionFactory.create(sparql, model)
      try {
        qe.execSelect()
      } finally {
        qe.close()
      }
    } finally {
      model.close()
    }
  }

}
