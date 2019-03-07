package com.knowledge.ui

import java.net.URL

import com.knowledge.ui.GraphMenu.queryMenu
import com.knowledge.ui.prefuse.GraphView
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.input
import scalafx.Includes._
import scalafx.application.JFXApp.PrimaryStage
import scalafx.application.{JFXApp, Platform}
import scalafx.event.subscriptions.Subscription
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.input.{InputEvent, KeyCombination, MouseEvent}
import scalafx.scene.layout.VBox
import scalafx.stage.Stage
import scalafxml.core.{FXMLView, NoDependencyResolver}

/**
  * Created by srikanth on 11/1/18.
  */
object GraphMenu extends JFXApp{

  stage = new PrimaryStage

  stage.title = "Graph-BI"

  val menu = new Menu("File")

  val queryMenu = new Menu("RunQuery")

  val sparql = new MenuItem("Sparql")

  queryMenu.items.add(sparql)

  val fxmlPath1 = "./fxml/sparql_Query.fxml"

  val fxml1: URL = getClass.getResource(fxmlPath1)
  import scalafx.Includes._

  sparql.onAction = new EventHandler[ActionEvent] {
    override def handle(t: ActionEvent): Unit = {
      Platform.runLater(new Runnable() {
        override def run() {
          val root1 = FXMLView(fxml1,NoDependencyResolver)
          val dialogStage = new Stage()
          dialogStage.title = "Run Query"
          dialogStage.scene = new Scene(root1)
          dialogStage.show()
        }
      })
    }
  }

  val new_ = new MenuItem("New")
  new_.accelerator =  KeyCombination.keyCombination("Ctrl+N")

  menu.items.add(new_)

  val file = new MenuItem("File")

  val fxmlPath = "./fxml/FileChooser.fxml"

  val fxml: URL = getClass.getResource(fxmlPath)

  file.onAction = new EventHandler[ActionEvent] {

    override def handle(event: ActionEvent) {

        Platform.runLater(new Runnable() {
          override def run() {
            val root1 = FXMLView(fxml,NoDependencyResolver)
            val dialogStage = new Stage()
            dialogStage.title = "File Upload"
            dialogStage.scene = new Scene(root1)
            dialogStage.show()
          }
        })
      }
  }

  file.accelerator = KeyCombination.keyCombination("Ctrl+O")
  menu.items.add(file)

  val exit = new MenuItem("Quit")
  exit.setAccelerator(KeyCombination.keyCombination("Ctrl+Q"))

  exit.onAction = new EventHandler[ActionEvent] {
    override def handle(event: ActionEvent) {
      Platform.exit()
    }
  }
  menu.items.add(exit)

  val menuBar = new MenuBar()

  menuBar.getMenus.addAll(menu,queryMenu)

  val vb: VBox = new VBox(menuBar)

  new_.onAction = new EventHandler[ActionEvent] {

    override def handle(event: ActionEvent) {

          new GraphView()
    }
  }

  stage.scene = new Scene(vb,1000,1000)

}
