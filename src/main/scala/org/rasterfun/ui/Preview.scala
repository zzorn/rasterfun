package org.rasterfun.ui

import javax.swing.JPanel
import org.rasterfun.component.Comp
import org.rasterfun.util.FastImage
import java.awt.{Graphics2D, Graphics}
import org.rasterfun.Area
import org.rasterfun.components.Empty
import java.awt.event.{ComponentEvent, ComponentAdapter}
import simplex3d.math.Vec2i

/**
 * Preview panel for a component.
 */
class Preview extends JPanel() {

  var scale: Float = 4f
  var center = Vec2i(0,0)

  private var _comp: Comp = new Empty()
  private var bitmap: FastImage = null

  addComponentListener(new ComponentAdapter {
    override def componentResized(e: ComponentEvent) {
      bitmap = null
      repaint()
    }
  })

  def comp = _comp

  def comp_=(c: Comp) {
    if (c != _comp) {
      _comp = c

      bitmap = null
      repaint()
    }
  }

  private def renderImage() {
    // TODO: Do / call this in a background thread
    val w = getWidth
    val h = getHeight
    bitmap = new FastImage(w, h)

    val averageScreenSize: Float = (w + h + 1f) * 0.5f
    val screenToWorldScale = scale / averageScreenSize
    val aw = screenToWorldScale * w
    val ah = screenToWorldScale * h
    val area = new Area(-aw/2 + center.x, -ah/2 + center.y, aw, ah)
    comp.render(bitmap.buffer, w, h, area)
  }

  override def paintComponent(g: Graphics) {
    val g2 = g.asInstanceOf[Graphics2D]

    if (bitmap == null) renderImage()

    if (bitmap != null) bitmap.renderToGraphics(g2)
  }

}