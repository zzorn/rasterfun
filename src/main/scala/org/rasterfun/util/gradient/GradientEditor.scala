package org.rasterfun.util.gradient

import simplex3d.math.float._
import simplex3d.math.float.functions._

import org.scalaprops.Property
import org.scalaprops.ui.{EditorFactory, Editor}
import javax.swing.JPanel
import java.awt._

/**
 * Interactive editor for gradients.
 */
// TODO: Interaction: click to add new midpoint,
// click color tab to open color selector, drag color tab to move it,
// drag color tab away to remove it.
class GradientEditor() extends Editor[Gradient] {

  var backgroundColor: Color = Color.GRAY
  var borderColor: Color = Color.BLACK
  var pointSize: Int = 24

  private val view = new GradientView()
  private val pointView = new PointView()

  buildUi()

  def gradient: Gradient = view.gradient

  private def buildUi() {
    setPreferredSize(new Dimension(128, 32))
    setLayout(new BorderLayout())
    add(view, BorderLayout.CENTER)
    add(pointView, BorderLayout.SOUTH)

    pointView.setPreferredSize(new Dimension(128, 24))
  }

  protected def onInit(initialValue: Gradient, name: String) {
    view.gradient = initialValue
    repaint()
  }

  protected def onExternalValueChange(oldValue: Gradient, newValue: Gradient) {
    view.gradient = newValue
    repaint()
  }

  class PointView extends JPanel {
    override def paintComponent(g: Graphics) {
      val g2 = g.asInstanceOf[Graphics2D]

      // Paint bg
      g2.setColor(backgroundColor)
      g2.fillRect(0,0,getWidth, getHeight)

      // Paint control points
      gradient.controlPoints foreach {gp: GradientPoint =>
        val cx = (gp.value * getWidth).toInt
        val x1 = cx - pointSize / 2
        val y1 = 0

        g2.setColor(gp.solidJavaColor)
        g2.fillRect(x1, y1, pointSize, pointSize)

        g2.setColor(borderColor)
        g2.drawRect(x1, y1, pointSize, pointSize)
      }
    }
  }
}


object GradientEditorFactory extends EditorFactory[Gradient] {

  protected def createEditorInstance = new GradientEditor()

}

