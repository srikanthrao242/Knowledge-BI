package com.knowledge.ui.menus


import com.knowledge.ui.GraphMenu
import scalafx.scene.control.{Menu, MenuBar, MenuItem, TextInputDialog}
import scalafx.Includes._

import scala.util.Try

object NamedGraphs {

  val menu: Menu = new Menu("NamedGraph")

  def addMenus(menuBar: MenuBar,graph:String): Unit ={
    val item = new MenuItem(graph)
    menu.items.addAll(item)
    item.onAction = handle({
      val limit = limitPopup()
      println(limit)
    })
  }

  def limitPopup(): Int ={
    val dialog = new TextInputDialog(defaultValue = "0") {
      initOwner(GraphMenu.stage)
      title = "KNOWLEDGE-BI"
      headerText = ""
      contentText = "Please enter limit :"
    }
    val result = dialog.showAndWait()
    result match {
      case Some(limit) =>
        Try{
          limit.toInt
        }.getOrElse(limitPopup())
      case None       => limitPopup()
    }
  }

  def addGraphs(graphs:List[String]): Unit ={
    graphs.foreach(addMenus(GraphMenu.menuBar,_))
  }

}
