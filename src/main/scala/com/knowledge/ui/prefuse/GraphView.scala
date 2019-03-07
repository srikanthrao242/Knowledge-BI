package com.knowledge.ui.prefuse

import java.util

import com.knowledge.ui.GraphMenu
import javax.swing.JFrame
import prefuse.action.{ActionList, RepaintAction}
import prefuse.{Constants, Display, Visualization}
import prefuse.action.assignment.{ColorAction, DataColorAction}
import prefuse.action.layout.graph.ForceDirectedLayout
import prefuse.activity.Activity
import prefuse.controls.{DragControl, PanControl, ZoomControl}
import prefuse.data.{Graph, Node, Tuple}
import prefuse.render.{DefaultRendererFactory, LabelRenderer}
import prefuse.util.{ColorLib, GraphLib, PrefuseConfig}
import prefuse.visual.VisualItem
import scalafx.application.Platform
import scalafx.embed.swing.SwingNode


class GraphView {

  var graph:Graph = new prefuse.data.Graph()
  graph.addColumn("lable",classOf[String])
  graph.addColumn("nodeKey",classOf[Int])

  //http://www.cip.ifi.lmu.de/~kammerga/prefuseDoc/HelloWorldNodeSize.html
  /*try{
    graph = new GraphMLReader().readGraph("http://prefuse.org/doc/manual/introduction/example/socialnet.xml")
  }catch {
    case e:Exception => e.printStackTrace()
  }*/



  val vis = new Visualization()
  vis.add("graph", graph)

  val node: Node = graph.addNode()
  node.setString("lable","Srikanth")
  node.setInt("nodeKey",1)
  val node1 = graph.addNode()
  node1.setString("lable","rao")
  node1.setInt("nodeKey",2)

  val r = new LabelRenderer("lable")
  r.setRoundedCorner(8, 8)





  vis.setRendererFactory(new DefaultRendererFactory(r))

  val palette = Array(ColorLib.rgb(255, 180, 180), ColorLib.rgb(190, 190, 255))
  val fill = new DataColorAction("graph.nodes", "lable",
    Constants.NOMINAL, VisualItem.FILLCOLOR, palette)
  val text = new ColorAction("graph.nodes",
    VisualItem.TEXTCOLOR, ColorLib.gray(0))
  val edges = new ColorAction("graph.edges",
    VisualItem.STROKECOLOR, ColorLib.gray(200))
  val color = new ActionList()
  color.add(fill)
  color.add(text)
  color.add(edges)


  val layout = new ActionList(Activity.INFINITY)
  layout.add(new ForceDirectedLayout("graph"))
  layout.add(new RepaintAction())

  vis.putAction("color", color)
  vis.putAction("layout", layout)

  val display = new Display(vis)
  display.setSize(720, 500) // set display size
  display.addControlListener(new DragControl()) // drag items around
  display.addControlListener(new PanControl())  // pan with background left-drag
  display.addControlListener(new ZoomControl())

  val frame = new JFrame("prefuse example")

  import javax.swing.JFrame

  /*frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  frame.add(display)
  frame.pack // layout components in window
  frame.setVisible(true)*/


  val swingNode = new SwingNode()

  import javax.swing.JPanel
  import javax.swing.SwingUtilities

  SwingUtilities.invokeLater(new Runnable() {
    override def run(): Unit = {
      val panel = new JPanel
      panel.add(display)
      swingNode.setContent(panel)
      Platform.runLater(new Runnable {
        override def run(): Unit = {
          GraphMenu.vb.children.add(swingNode)
        }
      })
    }
  })

  vis.run("color")  // assign the colors
  vis.run("layout")

}

