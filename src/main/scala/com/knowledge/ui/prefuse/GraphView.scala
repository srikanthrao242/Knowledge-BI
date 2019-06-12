/*

 * */
package com.knowledge.ui.prefuse

import java.awt.Dimension

import com.knowledge.server.database.entities._
import com.knowledge.server.util.IteratorResultSetQuerySolution
import com.knowledge.ui.GraphMenu
import com.knowledge.ui.menus.GraphLayouts
import edu.uci.ics.jung.algorithms.layout._
import edu.uci.ics.jung.graph.SparseMultigraph
import edu.uci.ics.jung.visualization.VisualizationViewer
import edu.uci.ics.jung.visualization.control.{DefaultModalGraphMouse, ModalGraphMouse}
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position
import org.apache.jena.query.{QuerySolution, ResultSet}
import org.eclipse.rdf4j.query.algebra.StatementPattern
import org.eclipse.rdf4j.query.algebra.helpers.StatementPatternCollector
import org.eclipse.rdf4j.query.parser.sparql.SPARQLParserFactory
import scalafx.application.Platform
import scalafx.embed.swing.SwingNode

import scala.util.hashing.MurmurHash3
import scala.collection.JavaConverters._

case class Patterns(
    subject: String,
    predicate: String,
    obj: String,
    context: String)

class GraphView {

  def getStatementPatterns(sparqlQuery: String): List[Patterns] = {
    val factory = new SPARQLParserFactory()
    val parser = factory.getParser
    val parsedQuery = parser.parseQuery(sparqlQuery, null)
    val collector = new StatementPatternCollector()
    val tupleExpr = parsedQuery.getTupleExpr
    tupleExpr.visit(collector)
    val statementPatterns: List[StatementPattern] =
      collector.getStatementPatterns.asScala.toList
    statementPatterns.map(v => {
      val sub =
        if (v.getSubjectVar.isConstant) v.getSubjectVar.getValue.stringValue()
        else v.getSubjectVar.getName
      val pre =
        if (v.getPredicateVar.isConstant)
          v.getPredicateVar.getValue.stringValue()
        else v.getPredicateVar.getName
      val obj =
        if (v.getObjectVar.isConstant) v.getObjectVar.getValue.stringValue()
        else v.getObjectVar.getName
      val cont =
        if (v.getContextVar.isConstant) v.getContextVar.getValue.stringValue()
        else v.getContextVar.getName
      Patterns(sub, pre, obj, cont)
    })
  }

  def createVerticesEdges(
      graph: SparseMultigraph[PNode, PLink],
      patterns: List[Patterns],
      qs: QuerySolution
    ): Unit = {
    val qVar = qs.varNames().asScala.toList
    patterns.foreach(pat => {
      val sub = pat.subject
      var subject: PNode = EmptyNode
      if (qVar.contains(sub)) {
        val id = MurmurHash3.stringHash(qs.get(sub).toString).toLong
        subject = KNode(id, qs.get(sub).toString)
        if (!graph.containsVertex(subject))
          graph.addVertex(subject)
      }

      val obj = pat.obj
      var objects: PNode = EmptyNode
      if (qVar.contains(obj)) {
        val id = MurmurHash3.stringHash(qs.get(obj).toString).toLong
        objects = KNode(id, qs.get(obj).toString)
        if (!graph.containsVertex(objects))
          graph.addVertex(objects)
      } else {
        val id = MurmurHash3.stringHash(obj).toLong
        objects = KNode(id, obj)
        if (!graph.containsVertex(objects))
          graph.addVertex(objects)
      }

      val pre = pat.predicate
      var link: PLink = EmptyLink
      if (qVar.contains(pre)) {
        val id = MurmurHash3.stringHash(qs.get(pre).toString).toLong
        link = KLink(id, qs.get(pre).toString)
        if (subject.toString != "empty" && objects.toString != "empty" && !graph
              .containsEdge(link))
          graph.addEdge(link, subject, objects)
      } else {
        val id = MurmurHash3.stringHash(pre).toLong
        link = KLink(id, pre)
        if (subject.toString != "empty" && objects.toString != "empty" && !graph
              .containsEdge(link))
          graph.addEdge(link, subject, objects)
      }

    })
  }

  def getGraph(
      resultSet: ResultSet,
      sparqlQuery: String
    ): SparseMultigraph[PNode, PLink] = {
    val graph: SparseMultigraph[PNode, PLink] =
      new SparseMultigraph[PNode, PLink]()
    val patterns = getStatementPatterns(sparqlQuery)

    val ib: Array[QuerySolution] = new IteratorResultSetQuerySolution(resultSet).toArray
    ib.foreach(qs => {
      createVerticesEdges(graph, patterns, qs)
    })
    graph
  }

  def getLayout(graph: SparseMultigraph[PNode, PLink]): Layout[PNode, PLink] =
    if (GraphLayouts.layout == "kk") {
      new KKLayout(graph)
    } else if (GraphLayouts.layout == "dag") {
      new DAGLayout(graph)
    } else if (GraphLayouts.layout == "isom") {
      new ISOMLayout(graph)
    } else {
      new CircleLayout(graph)
    }

  def createGraph(resultSet: ResultSet, sparqlQuery: String): Unit = {

    val graph = getGraph(resultSet, sparqlQuery)
    val layout: Layout[PNode, PLink] = getLayout(graph)
    layout.setSize(new Dimension(300, 300))
    val vv = new VisualizationViewer[PNode, PLink](layout)
    vv.setPreferredSize(new Dimension(1350, 1350))

    vv.getRenderContext.setVertexLabelTransformer(new ToStringLabeller())
    vv.getRenderContext.setEdgeLabelTransformer(new ToStringLabeller())
    vv.getRenderer.getVertexLabelRenderer.setPosition(Position.CNTR)

    val gm = new DefaultModalGraphMouse()
    gm.setMode(ModalGraphMouse.Mode.TRANSFORMING)
    vv.setGraphMouse(gm)
    vv.addKeyListener(gm.getModeKeyListener)

    layout.reset()

    val swingNode = new SwingNode()

    import javax.swing.JPanel
    import javax.swing.SwingUtilities

    SwingUtilities.invokeLater(new Runnable() {
      override def run(): Unit = {
        val panel = new JPanel
        panel.setMaximumSize(new Dimension(1350, 500))
        panel.add(vv)
        swingNode.setContent(panel)
        Platform.runLater(GraphMenu.addItemToVB(swingNode))
      }
    })
  }

}
