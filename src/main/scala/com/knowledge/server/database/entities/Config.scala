/**/
package com.knowledge.server.database.entities

import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType

import scala.util.Try

case class Config(input: String = "",
                  query: Seq[String],
                  print: Boolean = false,
                  algo: String = "",
                  numParts: Int = 0,
                  numIters: Int = 0)

abstract class PNode

case class KNode(id: Long, name: String) extends PNode {
  override def toString: String = ConfigUtils.toStringForGraph(name)
}

case object EmptyNode extends PNode {
  override def toString: String = "empty"
}

abstract class PLink

case class KLink(id: Long, name: String) extends PLink {
  override def toString: String = ConfigUtils.toStringForGraph(name)
}

case object EmptyLink extends PLink {
  override def toString: String = "empty"
}

case class KAlert(message: String, stage: PrimaryStage) extends Runnable {
  override def run(): Unit =
    new Alert(AlertType.Information) {
      initOwner(stage)
      title = "rust-keylock"
      contentText = message
    }.showAndWait()
}

object ConfigUtils {
  def toStringForGraph(name: String): String =
    Try {
      if (name.contains("#")) {
        name.substring(name.lastIndexOf("#") + 1)
      } else {
        name.substring(name.lastIndexOf("/") + 1)
      }
    }.getOrElse(if (name.contains("^^")) {
      name.substring(0, name.lastIndexOf("^^"))
    } else {
      name
    })
}
