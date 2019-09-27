/**/
package com.knowledge.ui.menus

import scalafx.scene.control.{Menu, MenuBar, MenuItem}
import scalafx.Includes._

object GraphLayouts {

  var layout = "circle"

  def addMenus(menuBar: MenuBar): Unit = {

    val layouts = new Menu("GraphLayouts")
    val circle = new MenuItem("Circle")
    circle.onAction = handle {
      layout = "circle"
    }
    val dag = new MenuItem("Dag")
    dag.onAction = handle {
      layout = "dag"
    }
    val isom = new MenuItem("ISOM")
    isom.onAction = handle {
      layout = "isom"
    }
    val kk = new MenuItem("KK")
    kk.onAction = handle {
      layout = "kk"
    }
    layouts.items.addAll(circle, dag, isom, kk)

    menuBar.getMenus.addAll(layouts)
  }

}
