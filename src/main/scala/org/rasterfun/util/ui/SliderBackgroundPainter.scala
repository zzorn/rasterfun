package org.rasterfun.util.ui

import java.awt.{Color, Graphics2D}
import simplex3d.math.float._
import org.rasterfun.util.ColorUtils
import simplex3d.math.floatx.functions._

trait SliderBackgroundPainter {
  def repaintOnValueChange: Boolean
  def paint(graphics: Graphics2D, width: Int, height: Int, relativeValue: Double, vertical: Boolean)
}


object DefaultSliderBackgroundPainter
        extends ColoredSliderBackgroundPainter(
          new Color(1f, 1f, 1f),
          new Color(0.3f, 0.6f, 0.6f))

case class ColoredSliderBackgroundPainter(sliderBgColor: Color, sliderColor: Color) extends SliderBackgroundPainter {
  def repaintOnValueChange = true

  def paint(graphics: Graphics2D,
            width: Int,
            height: Int,
            relativeValue: Double,
            vertical: Boolean) {
    graphics.setColor(sliderBgColor)
    graphics.fillRect(0,0,width, height)

    graphics.setColor(sliderColor)
    if (vertical) graphics.fillRect(0, (height * relativeValue).toInt, width, height)
    else          graphics.fillRect(0, 0, (width * relativeValue).toInt, height)
  }
}

class SliderBgLinePainter(primaryLineColor: Float => Vec4,
                          secondaryLineColor: Float => Vec4 = null,
                          squarePatternSize: Int = 12) extends SliderBackgroundPainter {
  def calculateColor(r: Float): Vec4 = {
    primaryLineColor(r)
  }

  def calculateSecondaryColor(r: Float): Vec4 = {
    secondaryLineColor(r)
  }

  def drawLine(graphics: Graphics2D, r: Float, x: Int, height: Int) {
    def vecToSolidColor(v: inVec4): Color = {
      // Force alpha to one
      ColorUtils.toJavaColor(max(v, Vec4.UnitW))
    }

    val primaryColor = vecToSolidColor(calculateColor(r))

    val s = squarePatternSize
    if (secondaryLineColor != null && s > 0) {
      // Square pattern
      val secondaryColor = vecToSolidColor(calculateSecondaryColor(r))

      var usePrimary = (x % (s * 2) < s)
      var y = 0
      while (y < height) {
        graphics.setColor((if (usePrimary) primaryColor else secondaryColor))
        graphics.drawLine(x, y, x, min(height, y + s))
        usePrimary = !usePrimary
        y += s
      }
    }
    else {
      // No pattern
      graphics.setColor(primaryColor)
      graphics.drawLine(x, 0, x, height)
    }
  }

  def repaintOnValueChange = true

  def paint(graphics: Graphics2D,
            width: Int,
            height: Int,
            relativeValue: Double,
            vertical: Boolean) {

    var x = 0
    while (x < width) {
      val r = 1.0f * x / width
      drawLine(graphics, r, x, height)

      x += 1
    }
  }

}

