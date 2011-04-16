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
class GroupView extends JPanel(new TreeLayoutManager(orientation = BottomToTop,
                                                     siblingGap = 20,
                                                     branchGap = 50,
                                                     layerGap = 60)) {

  private var _group: Group = null
  private var views: Map[Comp, GroupCompView] = Map[Comp, GroupCompView]()

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
    addWithChildren(group.root)
  }

  def addWithChildren(comp: Comp, parent: GroupCompView = null) {
    if (comp != null) {

      // Create view
      val view = new GroupCompView(this, comp, parent)

      // Store reference
      views += comp -> view

      // Add to UI
      add(view)

      // Add children
      comp.inputComponents foreach {c => addWithChildren(c, view) }
    }

    revalidate()
  }

  def removeWithChildren(comp: Comp) {
    if (comp != null) {

      // Remove view
      views.get(comp) foreach {view => remove(view)}

      // Remove reference
      views -= comp

      // Remove children
      comp.inputComponents foreach {c => removeWithChildren(c) }
    }

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

        val viewAnchor = Vec2i(view.getX + view.getWidth/2, view.getY)
        comp.inputComponents foreach {input =>
          views.get(input).foreach{ child =>
            val childAnchor = Vec2i(child.getX + child.getWidth/2, child.getY + child.getHeight)
            g2.drawLine(viewAnchor.x, viewAnchor.y, childAnchor.x, childAnchor.y)
          }
        }
      }
    }

    // Fill bacground
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

