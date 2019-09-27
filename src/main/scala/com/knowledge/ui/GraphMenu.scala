/**/
package com.knowledge.ui

import javafx.scene.Node
import scalafx.application.JFXApp.PrimaryStage
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.control.ScrollPane.ScrollBarPolicy
import scalafx.scene.layout.{BorderPane, VBox}

/**
  * Created by srikanth on 11/1/18.
  */
object GraphMenu extends JFXApp {

  import com.knowledge.ui.menus._
  import scalafx.scene.control._

  stage = new PrimaryStage

  stage.title = "KNOWLEDGE-BI"

  val menuBar: MenuBar = new MenuBar()

  KFile.addMenus(menuBar)

  RunQuery.addMenus(menuBar)

  GraphLayouts.addMenus(menuBar)

  Charts.addMenus(menuBar)

  val vb: VBox = new VBox(5)

  vb.prefWidthProperty().bind(stage.widthProperty())

  val borderPane = new BorderPane()
  borderPane.setTop(menuBar)

  val scrollPane = new ScrollPane()
  borderPane.setCenter(scrollPane)

  scrollPane.content = vb
  scrollPane.hbarPolicy = ScrollBarPolicy.Always
  scrollPane.vbarPolicy = ScrollBarPolicy.AsNeeded

  stage.scene = new Scene(borderPane, 1000, 1000)

  def addItemToVB(node: Node): Unit = {
    val separator = new Separator()
    separator.setMaxWidth(Double.MaxValue)
    vb.children.addAll(node, separator)
  }

}
