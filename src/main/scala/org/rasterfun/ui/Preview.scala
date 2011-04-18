package org.rasterfun.ui

import org.rasterfun.component.Comp
import org.rasterfun.util.FastImage
import org.rasterfun.Area
import org.rasterfun.components.Empty
import java.awt.event.{ComponentEvent, ComponentAdapter}
import simplex3d.math.Vec2i
import net.miginfocom.swing.MigLayout
import sas.swing.plaf.MultiLineShadowUI
import java.awt.Container._
import org.scalaprops.Property
import java.awt.{Dimension, Color, Graphics2D, Graphics}
import javax.swing.{BorderFactory, SwingConstants, JLabel, JPanel}
import javax.swing.border.{LineBorder, Border}

/**
 * Preview panel for a component.
 */
// TODO: Extract camera
class Preview(component: Comp = new Empty(), showTitle: Boolean = true, size: Int = 128) extends JPanel(new MigLayout()) {

  var scale: Float = 3f
  var center = Vec2i(0,0)

  private var _comp: Comp = null
  private var bitmap: FastImage = null
  private var title: JLabel = null
  private var _state: ViewState = Normal

  private def nameChangeListener(oldName: String, newName: String) {
    if (title != null) {
      title.setText(newName)
      repaint()
    }
  }


  comp = component

  initUi()

  def comp = _comp

  def comp_=(c: Comp) {
    require (c != null, "component should notify be null")

    if (c != _comp) {
      if (_comp != null) _comp.name.removeListener(nameChangeListener)

      _comp = c

      _comp.name.onValueChange(nameChangeListener)
      nameChangeListener(null, _comp.name())

      bitmap = null
      repaint()
    }
  }

  /**
   * State, used for various selection etc visualization.
   */
  def state: ViewState = _state

  /**
   * State, used for various selection etc visualization.
   */
  def state_=(state: ViewState) {
    _state = state

    updateBorder()
  }

  private def updateBorder() {
    // TODO: Good looking rounded border
    setBorder(new LineBorder(state.color, UiSettings.previewBorderSize))
    repaint()
  }

  private def initUi() {
    setPreferredSize(new Dimension(size, size))

    updateBorder()

    addComponentListener(new ComponentAdapter {
      override def componentResized(e: ComponentEvent) {
        bitmap = null
        repaint()
      }
    })

    if (showTitle) {
      val title = new JLabel(comp.name(), SwingConstants.CENTER)
      title.setUI(MultiLineShadowUI.labelUI)
      title.setForeground(Color.WHITE)
      title.setFont(title.getFont.deriveFont(UiSettings.graphComponentSize / 8f));
      title
      add(title, "dock north, width 100%, gaptop 3px")
    }
  }

  private def renderImage() {
    // TODO: Do / call this in a background thread
    if (comp != null) {
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
  }

  override def paintComponent(g: Graphics) {
    val g2 = g.asInstanceOf[Graphics2D]

    if (bitmap == null) renderImage()

    if (bitmap != null) bitmap.renderToGraphics(g2)
  }

}