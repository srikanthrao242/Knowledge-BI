package com.knowledge.server.database

import org.apache.jena.query.ResultSet

trait GraphServers {
  def sparql(query: String, table: Boolean, graph: Boolean): ResultSet
  def upload(path: String)
}
