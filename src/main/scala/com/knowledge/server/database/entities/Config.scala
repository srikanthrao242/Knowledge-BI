package com.knowledge.server.database.entities

import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType

case class Config(input: String = "", query: Seq[String] = null, print: Boolean = false, algo: String = "",
                  numParts: Int = 0, numIters: Int = 0)


case class KAlert(message: String,stage:PrimaryStage) extends Runnable {
  override def run(): Unit = {
    new Alert(AlertType.Information) {
      initOwner(stage)
      title = "rust-keylock"
      contentText = message
    }.showAndWait()
  }
}

