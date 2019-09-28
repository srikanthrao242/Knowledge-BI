/**/
package com.knowledge.ui.controllers

import java.net.URL

import scalafx.Includes._
import scalafx.scene.{Node, Scene}
import scalafx.stage.Stage
import scalafxml.core.{FXMLView, NoDependencyResolver}

import scala.collection.immutable.HashMap

class ScreensController {

  import ScreensController._

  def addScreen(name: String, scr: Node): Unit =
    screen += (name -> scr)

  def unloadScreen(name: String): Boolean =
    if (screen.contains(name)) {
      screen -= name
      true
    } else {
      false
    }

  def loadScreen(name: String, resource: String): Option[Stage] =
    try {
      val fxml: URL = getClass.getResource(resource)
      val root = FXMLView(fxml, NoDependencyResolver)
      val dialogStage = new Stage()
      dialogStage.title = name
      dialogStage.scene = new Scene(root)
      addScreen(name, root)
      Some(dialogStage)
    } catch {
      case _: Exception => None
    }

}

object ScreensController {

  var screen = new HashMap[String, Node]()

}
