package com.knowledge.ui.menus


import com.knowledge.ui.Util
import scalafx.application.Platform
import scalafx.scene.control.{Menu, MenuBar, MenuItem}
import scalafx.scene.input.KeyCombination
import scalafx.Includes._

object KFile {

  def addMenus(menuBar: MenuBar): Unit ={
    val menu: Menu = new Menu("File")
    val new_ = new MenuItem("New")
    new_.accelerator =  KeyCombination.keyCombination("Ctrl+N")
    menu.items.add(new_)

    Util.createMenuItemAndLoad("File","./fxml/FileChooser.fxml",menu,"Ctrl+F")
    Util.createMenuItemAndLoad("Open Triple Store","./fxml/catalogAndRepositorySelection.fxml",menu,"Ctrl+O")

    val exit = new MenuItem("Quit")
    exit.setAccelerator(KeyCombination.keyCombination("Ctrl+Q"))
    exit.onAction = handle{Platform.exit()}
    menu.items.add(exit)

    menuBar.getMenus.addAll(menu)

  }

}
