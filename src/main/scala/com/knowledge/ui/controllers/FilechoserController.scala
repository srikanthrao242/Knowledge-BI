package com.knowledge.ui.controllers

import com.knowledge.server.database.fuseki.Fuseki
import org.apache.jena.riot.RDFLanguages
import scalafx.event.ActionEvent
import scalafx.scene.control.{Button, Label, TextField}
import scalafx.scene.layout.HBox
import scalafx.stage.{FileChooser, Stage}
import scalafxml.core.macros.sfxml

/**
  * Created by srikanth on 11/4/18.
  */

@sfxml
class FilechoserController(
  private var tablename: TextField,
  private var filepath: TextField,
  private var hbox: HBox,
  private var filechoose: Button,
  private var uploadlabel: Label) {

  def saveParquet(event: ActionEvent) {
    if (!filepath.getText().isEmpty) {
      val lang = RDFLanguages.filenameToLang(filepath.getText)
      println(lang.getName)
      new TableCreation().createTable(tablename.getText, filepath.getText)
      (new Fuseki).upload(filepath.getText)
    }
  }

  def extrachFile(event: ActionEvent) {
    val fileChooser = new FileChooser()
    val file = Option(fileChooser.showOpenDialog(new Stage()))
    file match {
      case Some(f) => filepath.setText(f.getAbsolutePath)
      case None => "File not selected."
    }
  }

}
