package com.spr.io


import com.spr.SparkCore.SparkCoreModule
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.io.LongWritable
import org.apache.jena.hadoop.rdf.io.input.rdfxml.RdfXmlInputFormat
import org.apache.jena.hadoop.rdf.io.registry.HadoopRdfIORegistry
import org.apache.jena.hadoop.rdf.io.registry.readers.RdfXmlReaderFactory
import org.apache.jena.hadoop.rdf.types.TripleWritable

import org.apache.jena.query.Query


object JenaReader extends SparkCoreModule with App{

  val factory = new RdfXmlReaderFactory()
  HadoopRdfIORegistry.addReaderFactory(factory)
  val conf = new Configuration()
  conf.set("rdf.io.input.ignore-bad-tuples", "false")
  val path = "/home/leadsemantics-08/work/product/development/hiddime/hiddime-1.0.4/hd-child/server/temp/Emp_DetailsData_2.rdf"
  val data = SPARK_CONTEXT.newAPIHadoopFile(path,
    classOf[RdfXmlInputFormat],
    classOf[LongWritable], //position
    classOf[TripleWritable],   //value
    conf)

  val query = new Query()

  data.foreach{
    case (l,t)=> println(t.get().getSubject)
  }
  //data.take(10).foreach(println)

}
