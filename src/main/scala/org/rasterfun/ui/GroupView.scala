package org.rasterfun.ui

import org.rasterfun.components.Group
import org.rasterfun.component.Comp
import javax.swing.{BorderFactory, JPanel}
import simplex3d.math.Vec2i
import java.awt._
import org.rasterfun.util.treelayout.{BottomToTop, TreeLayoutManager}

/**
 * View of a group of nodes.
 */
class GroupView extends JPanel(new TreeLayoutManager(orientation = BottomToTop,
                                                     siblingGap = 20,
                                                     branchGap = 40,
                                                     layerGap = 60)) {

  private var _group: Group = null
  private var views: Map[Comp, CompView] = Map[Comp, CompView]()

  val bgColor = UiSettings.componentViewBackgroundColor
  val connectionColor = Color.WHITE
  val edgeColor = Color.DARK_GRAY
  val connectionWidth = 2
  val edge = 2

  private val connectionInnerStroke = new BasicStroke(connectionWidth)
  private val connectionOuterStroke = new BasicStroke(connectionWidth + edge)

  def group = _group

  def group_= (group: Group) {
    // Remove old
    views.values foreach (v => remove(v))

    _group = group

    if (group != null) addComponents(group)
  }

  private def addComponents(group: Group) {

    def addWithChildren(comp: Comp, parent: CompView) {
      if (comp != null) {

        // Create view
        val view = new CompView(comp, parent)

        // Store reference
        views += comp -> view

        // Add to UI
        add(view)

        // Add children
        comp.inputComponents foreach {c => addWithChildren(c, view) }
      }
    }

    addWithChildren(group.root, null)

    revalidate()
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

        val viewAnchor = Vec2i(view.getX + view.getWidth/2, view.getY + view.getHeight)
        comp.inputComponents foreach {input =>
          views.get(input).foreach{ child =>
            val childAnchor = Vec2i(child.getX + child.getWidth/2, child.getY)
            g2.drawLine(viewAnchor.x, viewAnchor.y, childAnchor.x, childAnchor.y)
          }
        }
      }
    }

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

