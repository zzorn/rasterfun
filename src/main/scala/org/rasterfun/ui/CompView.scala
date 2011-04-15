package org.rasterfun.ui

import net.miginfocom.swing.MigLayout
import org.rasterfun.component.Comp
import simplex3d.math.Vec2i
import org.rasterfun.util.FastImage
import org.rasterfun.Area
import java.awt.{Color, Graphics2D, Graphics, Dimension}
import org.rasterfun.util.treelayout.TreeNodeComponent
import javax.swing.{JLabel, SwingConstants, JPanel}
import sas.swing.plaf.MultiLineShadowUI

/**
 * 
 */
class CompView(component: Comp, parentView: java.awt.Component) extends RichPanel(border = true) with TreeNodeComponent {

  var area: Area = new Area(-1, -1, 3, 3)

  private var bitmap: FastImage = null

  setPreferredSize(UiSettings.componentSize)
  val title = new JLabel(component.name, SwingConstants.CENTER)
  title.setUI(MultiLineShadowUI.labelUI)
  title.setForeground(Color.WHITE)
  title.setFont(title.getFont.deriveFont(16f));
  add(title, "dock north, width 100%, gaptop 3px")


  override def parentTreeNode = parentView

  private def renderImage() {
    // TODO: Do / call this in a background thread
    val w = UiSettings.componentWidth
    val h = UiSettings.componentHeight
    bitmap = new FastImage(w, h)

    component.render(bitmap.buffer, w, h, area)
  }

  override def paintComponent(g: Graphics) {
    val g2 = g.asInstanceOf[Graphics2D]

    if (bitmap == null) renderImage()

    bitmap.renderToGraphics(g2)
  }
}