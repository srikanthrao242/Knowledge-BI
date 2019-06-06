package com.knowledge.ui.menus

import com.knowledge.ui.Util
import scalafx.scene.control.{Menu, MenuBar}

object RunQuery {

  def addMenus(menuBar: MenuBar): Unit = {
    val menu = new Menu("RunQuery")
    Util.createMenuItemAndLoad("Sparql", "./fxml/sparql_Query.fxml", menu, "")
    menuBar.getMenus.addAll(menu)
  }

}
