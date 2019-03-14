package com.knowledge.server.database.entities

import java.net.URL

import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType

case class Config(input: String = "", query: Seq[String] = null, print: Boolean = false, algo: String = "",
                  numParts: Int = 0, numIters: Int = 0)



abstract class PNode

case class KNode(id:Long,name:String) extends PNode{
  override def toString: String ={
    try{
      var a = new URL(name)
      if(name.contains("#")){
        name.substring(name.lastIndexOf("#"))
      }else{
        name.substring(name.lastIndexOf("/"))
      }
    }catch {
      case a:Exception =>
        if(name.contains("^^"))
          name.substring(0,name.lastIndexOf("^^"))
        else name
    }
  }
}

case object EmptyNode extends PNode {
  override def toString = "empty"
}

abstract class PLink

case class KLink(id:Long,name:String) extends PLink{
  override def toString: String ={
    try{
      var a = new URL(name)
      if(name.contains("#")){
        name.substring(name.lastIndexOf("#")+1)
      }else{
        name.substring(name.lastIndexOf("/")+1)
      }
    }catch {
      case a:Exception =>
        if(name.contains("^^"))
          name.substring(0,name.lastIndexOf("^^"))
        else name
    }
  }
}

case object EmptyLink extends PLink{
  override def toString = "empty"
}


case class KAlert(message: String,stage:PrimaryStage) extends Runnable {
  override def run(): Unit = {
    new Alert(AlertType.Information) {
      initOwner(stage)
      title = "rust-keylock"
      contentText = message
    }.showAndWait()
  }
}

