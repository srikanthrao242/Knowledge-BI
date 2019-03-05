package com.knowledge.ui.controllers

import com.knowledge.server.database.CreateSchemas
import javafx.collections.{FXCollections, ObservableList}
import javafx.scene.control.cell.PropertyValueFactory
import com.knowledge.server.sparkCore.SparkCoreModule
import com.knowledge.server.util.ReadRDF
import com.knowledge.ui.GraphMenu
import org.apache.jena.graph.Triple
import org.apache.spark.rdd.RDD
import scalafx.scene.control.{TableCell, TableColumn, TableView}
import scalafx.collections.ObservableBuffer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import scalafx.application.Platform
import scalafx.scene.control.TableColumn._

import collection.JavaConverters._

case class TripleClass(Subject:String,Predicate:String,Object:String)

class TableCreation extends SparkCoreModule{

  def createTriplesTable[T](tableView: TableView[T],columns:List[String],data:ObservableBuffer[T]): TableView[T] ={
    tableView.setItems(data)
    columns.foreach(v=> {
      val col = new TableColumn[T,String]()
      col.text = v
      col.setCellValueFactory(new PropertyValueFactory[T,String](v))
      tableView.columns += col
    })
    tableView.setItems(data)
    tableView
  }

  def spoTableCreation(data:Array[TripleClass]):Unit={
    val fullData: ObservableBuffer[TripleClass] = ObservableBuffer(data:_*)
    val columns = List("Subject","Predicate","Object")
    val tableView = createTriplesTable[TripleClass](new TableView[TripleClass](),columns,fullData)
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
      println(path)
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
          TripleClass(subj,pre,obj)
        }).collect()
        spoTableCreation(arr)
      case Failure(ex) => println(ex)
    }
  }

}
