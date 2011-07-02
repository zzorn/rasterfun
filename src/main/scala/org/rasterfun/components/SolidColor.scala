package org.rasterfun.components

import org.rasterfun.component.Comp

import simplex3d.math._
import simplex3d.math.float._
import simplex3d.math.float.functions._
import org.rasterfun.util.ColorUtils
import org.scalaprops.ui.editors.SliderFactory._
import org.scalaprops.ui.editors.{ColoredSliderBackgroundPainter, SliderFactory}
import java.awt.Color
import org.scalaprops.Property
import org.rasterfun.util.coloreditor.ColorEditorFactory

/**
 * A simple solid color component.
 */
class SolidColor(r: Float = 1f, g: Float = 0f, b: Float = 0f, a: Float = 1f) extends Comp {

  val color = p('color, Vec4(1, 1, 1, 1)).editor(new ColorEditorFactory(true)).onChange( updateColor _  )

  private var _intensity: Float = 1f

  updateColor()

  override protected def createCopy = new SolidColor()

  private def updateColor() {
    val c = color()
    _intensity = (c.r + c.g + c.b) / 3f
  }

  override def intensity(pos: inVec2) = _intensity

  override def channel(channel: Symbol, pos: inVec2): Float = {
    channel match {
      case 'intensity => _intensity
      case 'red => color().r
      case 'green => color().g
      case 'blue => color().b
      case 'alpha => color().a
      case 'hue => ColorUtils.hue(color())
      case 'saturation => ColorUtils.saturation(color())
      case 'lightness => ColorUtils.lightness(color())
      case _ => 0f
    }
  }

  def rgba(pos: inVec2) = color()
}