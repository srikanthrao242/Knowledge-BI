package com.knowledge.server.util

import org.apache.jena.query.{QuerySolution, ResultSet}

class IteratorResultSetQuerySolution(rs: ResultSet) extends Iterator[QuerySolution]{

  override def hasNext: Boolean = rs.hasNext

  override def next(): QuerySolution = rs.next()

}
