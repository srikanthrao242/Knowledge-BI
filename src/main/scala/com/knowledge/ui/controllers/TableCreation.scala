package com.knowledge.ui.controllers

import com.knowledge.server.database.CreateSchemas
import com.knowledge.server.sparkCore.SparkCoreModule
import com.knowledge.server.util.ReadRDF
import com.knowledge.ui.GraphMenu
import javafx.beans.property.SimpleStringProperty
import org.apache.jena.graph.Triple
import org.apache.spark.rdd.RDD
import scalafx.scene.control.{TableColumn, TableView}
import scalafx.collections.ObservableBuffer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import scalafx.application.Platform
import scalafx.scene.control.TableColumn._
import scalafx.Includes._


class TableCreation extends SparkCoreModule{

  def createTriplesTable(columns:List[String],data:ObservableBuffer[Array[String]]): TableView[Array[String]]={
    val tableView = new TableView[Array[String]](data)
    columns.indices.foreach(i => {
      val v = columns(i)
      val col = new TableColumn[Array[String], String]()
      col.text = v
      col.cellValueFactory = {
        g => new SimpleStringProperty(g.value(i))
      }
      tableView.columns += col
    })
    tableView
  }
  def spoTableCreation(data:Array[Array[String]]):Unit={
    val fullData: ObservableBuffer[Array[String]] = ObservableBuffer(data:_*)
    val columns = List("Subject","Predicate","Object")
    val tableView = createTriplesTable(columns,fullData)
    Platform.runLater(new Runnable() {
      def run() {
        GraphMenu.vb.children.add(tableView)
      }
    })
  }

  def saveFile(catalog:String,repository:String,df:RDD[Triple]):Boolean = {
    try{
      var path = if(catalog.isEmpty)
        CreateSchemas.getDefaultCatalog
      else
        CreateSchemas.getWareHousePath+catalog
      path = path + (if(repository.isEmpty) ""; else "/"+repository)
      df.saveAsObjectFile(path)
      true
    }catch {
      case ex:Exception=>ex.printStackTrace()
        false
    }
  }

  def createTable(repository:String,path:String):Unit={
    ReadRDF.readNtriples(path).onComplete {
      case Success(v)  =>
        saveFile("",repository,v)
        val arr = v.map(t=>{
          val subj = if(t.getSubject.isBlank) t.getSubject.getBlankNodeLabel
          else t.getSubject.getURI
          val pre = t.getPredicate.getURI
          val obj = if(t.getObject.isBlank) t.getObject.getBlankNodeLabel
          else if(t.getObject.isURI) t.getObject.getURI
          else t.getObject.getLiteral.toString()
          Array(subj,pre,obj)
        }).collect()
        spoTableCreation(arr)
      case Failure(ex) => println(ex.getMessage)
    }
  }

}
