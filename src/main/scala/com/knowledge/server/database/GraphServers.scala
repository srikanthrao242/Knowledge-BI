/*

 * */
package com.knowledge.server.database

import org.apache.jena.query.ResultSet

trait GraphServers {
  def sparql(query: String, table: Boolean, graph: Boolean): Option[ResultSet]
  def upload(grpahName: String, path: String)
}
