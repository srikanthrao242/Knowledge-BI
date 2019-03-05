package com.knowledge.server.database.entities

case class Config(input: String = "", query: Seq[String] = null, print: Boolean = false, algo: String = "",
                  numParts: Int = 0, numIters: Int = 0)

