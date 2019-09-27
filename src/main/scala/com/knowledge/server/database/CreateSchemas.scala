/**/
package com.knowledge.server.database

import java.io.File
import java.nio.file.Files

import com.knowledge.server.sparkCore.SparkCoreModule
import org.apache.hadoop.fs.{FileSystem, Path}

class CreateSchemas extends SparkCoreModule {

  import CreateSchemas._
  def createCatalog(name: String): Boolean =
    if (hdfsEnable) {
      val fs = FileSystem.get(SPARK_CONTEXT.hadoopConfiguration)
      if (fs.exists(new Path(HDFS_PATH + name))) {
        true
      } else {
        fs.mkdirs(new Path(HDFS_PATH + name))
      }
    } else {
      if (Files.exists(java.nio.file.Paths.get(getWareHousePath + "/" + name))) {
        true
      } else {
        val fs: File = new File(getWareHousePath + "/" + name)
        fs.exists()
      }
    }

  def createRepository(catalog: String, name: String): Boolean =
    if (hdfsEnable) {
      val fs = FileSystem.get(SPARK_CONTEXT.hadoopConfiguration)
      if (fs.exists(new Path(HDFS_PATH + catalog + "/" + name))) {
        true
      } else {
        fs.mkdirs(new Path(HDFS_PATH + catalog + "/" + name))
      }
    } else {
      if (Files.exists(
            java.nio.file.Paths
              .get(getWareHousePath + "/" + catalog + "/" + name)
          )) {
        true
      } else {
        val fs: File = new File(getWareHousePath + "/" + catalog + "/" + name)
        fs.exists()
      }
    }

  def listCatalogs: List[String] = {
    val wh = new File(getWareHousePath)
    if (wh.isDirectory) {
      wh.listFiles().map(_.getAbsolutePath).toList
    } else {
      Nil
    }
  }

  def listRepositories(catalog: String): List[String] = {
    val cata = new File(getWareHousePath + "/" + catalog)
    if (cata.isDirectory) {
      cata.listFiles().map(_.getAbsolutePath).toList
    } else {
      Nil
    }
  }

}

object CreateSchemas {

  val hdfsEnable = false
  val HDFS_PATH = "hdfs://"
  val getWareHousePath = new File(
    System.getProperty("user.dir") + "/spark-warehouse/"
  ).getAbsolutePath
  val getDefaultCatalog = new File(
    System.getProperty("user.dir") + "/spark-warehouse/Default"
  ).getAbsolutePath

}
