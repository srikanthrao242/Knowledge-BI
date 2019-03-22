package com.knowledge.ui.menus

import java.net.URL

import com.knowledge.ui.GraphMenu
import scalafx.scene.control.{Menu, MenuBar, MenuItem}
import scalafx.Includes._
import scalafx.application.Platform
import scalafxml.core.{FXMLView, NoDependencyResolver}

object Charts {

  def addMenus(menuBar: MenuBar): Unit={
    val menu: Menu = new Menu("Charts")
    val menuItem = new MenuItem("Charts")
    val fxmlPath = "../fxml/Charts.fxml"
    val fxml: URL = getClass.getResource(fxmlPath)
    menuItem.onAction = handle{
      Platform.runLater(loadFXML(fxml))
    }
    menu.items.addAll(menuItem)
    menuBar.getMenus.addAll(menu)
  }

  def loadFXML(fxml: URL): Unit ={
    val root = FXMLView(fxml, NoDependencyResolver)
    GraphMenu.addItemToVB(root)
  }

}
