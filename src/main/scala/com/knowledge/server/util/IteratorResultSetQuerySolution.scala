package com.knowledge.server.util

import org.apache.jena.query.{QuerySolution, ResultSet}
import org.apache.jena.graph.Triple

class IteratorResultSetQuerySolution(rs: ResultSet) extends Iterator[QuerySolution]{

  override def hasNext: Boolean = rs.hasNext

  override def next(): QuerySolution = rs.next()

}

class IteratorResultSetTriples(rs: ResultSet) extends Iterator[Triple]{

  override def hasNext: Boolean = rs.hasNext

  override def next(): Triple = {
    val qs = rs.next()
    Triple.create(qs.get("s").asNode(),qs.get("p").asNode(),qs.get("o").asNode())
  }
}

class IteratorResultSetGraphString(rs: ResultSet) extends Iterator[String]{

  override def hasNext: Boolean = rs.hasNext

  override def next(): String = {
    val qs = rs.next()
    qs.get("g").toString
  }
}

