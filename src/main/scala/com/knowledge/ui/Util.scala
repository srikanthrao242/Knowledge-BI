package com.knowledge.ui

import java.net.URL

import scalafx.scene.Scene
import scalafx.stage.Stage
import scalafxml.core.{FXMLView, NoDependencyResolver}
import scalafx.Includes._
import scalafx.application.Platform
import scalafx.scene.control.{Menu, MenuItem}
import scalafx.scene.input.KeyCombination

object Util {

  def loadFXML(fxml: URL): Unit ={
    val root = FXMLView(fxml, NoDependencyResolver)
    val dialogStage = new Stage()
    dialogStage.title = "File Upload"
    dialogStage.scene = new Scene(root)
    dialogStage.show()
  }

  def createMenuItemAndLoad(menuName:String,fxmlPath:String, menu:Menu,acceleratior: String): Unit ={
    val mi = new MenuItem(menuName)
    val fxml: URL = getClass.getResource(fxmlPath)
    if(fxmlPath.nonEmpty) mi.onAction = handle{Platform.runLater(Util.loadFXML(fxml))}
    if(acceleratior.nonEmpty) mi.accelerator = KeyCombination.keyCombination(acceleratior)
    menu.items.add(mi)
  }


}
