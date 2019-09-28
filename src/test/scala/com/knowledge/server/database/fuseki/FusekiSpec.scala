/*

 * */
package com.knowledge.server.database.fuseki

import org.scalatest.FlatSpec

class FusekiSpec extends FlatSpec {

  "getConnection" should "give connection" in {
    Fuseki.dataAccessMode = "http"
    Fuseki.serviceUri = "http://localhost:3030/sample"
    Fuseki.path = "/sample"
    val f = new Fuseki
    // f.upload("http://www.knowldegeBi.com/example", "F:\\IdeaProjects\\Knowledge-BI\\dataset\\rdf.nt")
  }

  "get Sparql" should "give result" in {
    Fuseki.dataAccessMode = "http"
    Fuseki.serviceUri = "http://localhost:3030/sample"
    val f = new Fuseki
    f.sparql("select ?s ?p ?o{?s ?p ?o}", table = false, graph = false)
  }
}
