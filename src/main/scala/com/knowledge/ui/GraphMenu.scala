package com.knowledge.ui

import scalafx.application.JFXApp.PrimaryStage
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.control.ScrollPane.ScrollBarPolicy
import scalafx.scene.control._
import scalafx.scene.layout.VBox

/**
  * Created by srikanth on 11/1/18.
  */
object GraphMenu extends JFXApp{

  import com.knowledge.ui.menus._

  stage = new PrimaryStage

  stage.title = "KNOWLEDGE-BI"

  val menuBar: MenuBar = new MenuBar()

  KFile.addMenus(menuBar)

  RunQuery.addMenus(menuBar)

  val vb: VBox = new VBox()

  vb.prefWidthProperty().bind(stage.widthProperty())

  vb.children.addAll(menuBar)

  val scrollPane = new ScrollPane()

  scrollPane.content = vb
  scrollPane.hbarPolicy = ScrollBarPolicy.Always
  scrollPane.vbarPolicy = ScrollBarPolicy.AsNeeded
  
  stage.scene = new Scene(scrollPane,1000,1000)

}
