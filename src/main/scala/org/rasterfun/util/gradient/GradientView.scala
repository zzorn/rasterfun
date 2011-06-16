package org.rasterfun.util.gradient

import javax.swing.{JPanel, JComponent}
import simplex3d.math.float._
import simplex3d.math.float.functions._
import java.awt.{Color, Graphics2D, Graphics}
import sas.swing.GradientPanel

/**
 * An UI component that shows a gradient.
 */
class GradientView() extends JPanel {

  var alphaGridSize = 16
  var alphaGrey1 = 0.6f
  var alphaGrey2 = 0.4f
  var emptyColor = Color.GRAY

  var start = 0f
  var end = 1f

  private var _gradient: Gradient = null

  def gradient_=(g: Gradient) {
    _gradient = g
    repaint()
  }

  def gradient: Gradient = _gradient

  private def makeColor(relativePos: Float, greyLevel: Float): Color = {
    val g = gradient.apply(relativePos)
    new Color(
      mix(greyLevel, g.r, g.a),
      mix(greyLevel, g.g, g.a),
      mix(greyLevel, g.b, g.a)
    )
  }

  override def paintComponent(g: Graphics) {
    val g2 = g.asInstanceOf[Graphics2D]

    val w = getWidth
    val h = getHeight

    if (gradient == null) {
      g2.setColor(emptyColor)
      g2.fillRect(0, 0, w, h)
    }
    else {
      var x = 0
      while (x < w) {

        val relativePos = start + (1.0f * x / w) * (end - start)
        val c1 = makeColor(relativePos, alphaGrey1)
        val c2 = makeColor(relativePos, alphaGrey2)

        var y = 0
        var gridSelect = ((x % alphaGridSize) % 2) == 0
        while (y < h) {
          val y2 = min(h, y + alphaGridSize - 1)

          if (y2 > y) {
            if (gridSelect) g2.setColor(c1)
            else g2.setColor(c2)

            g2.drawLine(x, y, x, y2)
          }

          y += alphaGridSize
          gridSelect = !gridSelect
        }

        x += 1
      }
    }
  }

}