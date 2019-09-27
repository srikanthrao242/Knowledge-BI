/*

 * */
package com.knowledge.server.database.fuseki

import java.io.File

import com.knowledge.server.database.GraphServers
import com.knowledge.ui.controllers.TableCreation
import com.knowledge.ui.prefuse.GraphView
import org.apache.http.impl.client.BasicResponseHandler
import org.apache.http.{HttpHost, HttpResponse}
import org.apache.http.message.BasicHttpRequest
import org.apache.jena.query.{DatasetAccessorFactory, QueryExecutionFactory, ResultSet}
import org.apache.jena.rdf.model.{Model, ModelFactory}
import org.apache.jena.rdfconnection.{RDFConnection, RDFConnectionFactory}
import org.apache.jena.riot.RDFLanguages
import org.apache.jena.riot.web.HttpOp
import org.apache.jena.tdb.TDBFactory
import spray.json._
import DefaultJsonProtocol._

import scala.util.Try

case class DsArray(`ds.name`: String, `ds.state`: Boolean)
case class DS(datasets: List[DsArray])

object DsImplicits {
  implicit val dsArrjson = jsonFormat2(DsArray)
  implicit val dsjson = jsonFormat1(DS)
}

class Fuseki extends GraphServers {
  import Fuseki._

  private val LOCAL: String = "local"
  private val HTTP: String = "http"
  private val model: Option[Model] =
    if (LOCAL.equalsIgnoreCase(dataAccessMode)) {
      val ds = TDBFactory.createDataset(path)
      Some(ds.getNamedModel(modelName))
    } else if (HTTP.equalsIgnoreCase(dataAccessMode)) {
      val dsAccessor =
        DatasetAccessorFactory.createHTTP(serviceUri + s"/$dataset")
      Some(dsAccessor.getModel())
    } else {
      None
    }

  private[fuseki] def getConnection: RDFConnection =
    RDFConnectionFactory.connect(serviceUri)

  def getDs: Option[String] = {
    val client = HttpOp.getDefaultHttpClient
    Try {
      val requet = new BasicHttpRequest("GET", "/$/datasets")
      val resp: HttpResponse =
        client.execute(HttpHost.create(serviceUri), requet)
      new BasicResponseHandler().handleResponse(resp)
    }.toOption
  }

  def putDs(dsName: String): Option[HttpResponse] = {
    val client = HttpOp.getDefaultHttpClient
    Try {
      val request =
        new BasicHttpRequest("POST", "/$/datasets?dbType=tdb&dbName=" + dsName)
      client.execute(HttpHost.create(serviceUri), request)
    }.toOption
  }

  override def upload(graphName: String, path: String): Unit = {
    val file = new File(path)
    val model = ModelFactory.createDefaultModel()
    val lang = RDFLanguages.filenameToLang(path)
    import java.io.FileInputStream
    try {
      val in = new FileInputStream(file)
      try model.read(in, "", lang.getName)
      finally if (in != null) in.close()
    }
    val accessor = DatasetAccessorFactory
      .createHTTP(serviceUri)
    accessor.putModel(model)
  }

  override def sparql(
    query: String,
    table: Boolean,
    graph: Boolean
  ): Option[ResultSet] =
    try {
      val qe =
        QueryExecutionFactory.sparqlService(serviceUri + s"/$dataset", query)
      try {
        val results: ResultSet = qe.execSelect()
        if (table) new TableCreation().createTableOfResultSet(results)
        if (graph) new GraphView().createGraph(results, query)
        Some(results)
      } catch {
        case e: Exception =>
          e.printStackTrace()
          None
      } finally {
        qe.close()
      }
    } catch {
      case e: Exception =>
        e.printStackTrace()
        None
    }

}

object Fuseki {
  var modelName: String = ""
  var path: String = ""
  var dataAccessMode: String = ""
  var serviceUri: String = ""
  var dataset: String = ""
}
