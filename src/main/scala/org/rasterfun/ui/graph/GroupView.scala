package org.rasterfun.ui.graph

import org.rasterfun.components.Group
import org.rasterfun.component.Comp
import javax.swing.{JPanel}
import simplex3d.math.Vec2i
import java.awt._
import org.rasterfun.util.treelayout.{BottomToTop, TreeLayoutManager}
import org.rasterfun.ui.{UiSettings}

/**
 * View of a group of nodes.
 */
// TODO: Move component connection drawing to tree layout manager related helper class
class GroupView extends JPanel(new TreeLayoutManager(orientation = BottomToTop,
                                                     siblingGap = 20,
                                                     branchGap = 50,
                                                     layerGap = 60)) {

  private var _group: Comp = null
  private var views: Map[Comp, GroupCompView] = Map[Comp, GroupCompView]()

  val bgColor = UiSettings.componentViewBackgroundColor
  val connectionColor = Color.WHITE
  val edgeColor = Color.DARK_GRAY
  val connectionWidth = 2
  val edge = 4

  private val connectionInnerStroke = new BasicStroke(connectionWidth)
  private val connectionOuterStroke = new BasicStroke(connectionWidth + edge)

  def group = _group

  private val structureChangeListener: ((Comp) => Unit) =  { c => group_=(c)  }

  def group_= (comp: Comp) {
    // Remove old
    if (_group != null) _group.removeStructureListener(structureChangeListener)
    views = Map[Comp, GroupCompView]()
    removeAll()

    _group = comp

    if (_group != null) {
      addWithChildren(_group)

      // If the structure changes, call this method to remove the old views and add new ones using the specified new root.
      _group.addStructureListener(structureChangeListener)
    }

    revalidate()
  }

  private def addWithChildren(comp: Comp, parent: GroupCompView = null) {
    // Create view
    val view = new GroupCompView(this, comp, parent)

    // Store reference
    views += comp -> view

    // Add to UI
    add(view)

    // Add children
    comp.inputComponents foreach {c => addWithChildren(c, view) }
  }


  override def paintComponent(g: Graphics) {
    val g2 = g.asInstanceOf[Graphics2D]

    // Store context
    val antiAlias = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING)
    val oldStroke = g2.getStroke

    def drawConnectionLines(color: Color, stroke: Stroke) {
      g2.setColor(color)
      g2.setStroke(stroke)

      views foreach {v =>
        val comp = v._1
        val view = v._2

        val viewAnchor = Vec2i(view.getX + view.getWidth/2, view.getY)
        comp.inputComponents foreach {input =>
          views.get(input).foreach{ child =>
            val childAnchor = Vec2i(child.getX + child.getWidth/2, child.getY + child.getHeight)
            g2.drawLine(viewAnchor.x, viewAnchor.y, childAnchor.x, childAnchor.y)
          }
        }
      }
    }

    // Fill background
    g2.setColor(UiSettings.componentViewBackgroundColor)
    g2.fillRect(0, 0, getWidth, getHeight)

    // Smooth edges to lines
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

    // Draw connection lines
    drawConnectionLines(edgeColor, connectionOuterStroke) // Outline
    drawConnectionLines(connectionColor, connectionInnerStroke) // Inner color

    // Restore render state
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAlias)
    g2.setStroke(oldStroke)
  }
}

