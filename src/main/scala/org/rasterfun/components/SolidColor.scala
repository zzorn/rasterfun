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

/**
 * A simple solid color component.
 */
class SolidColor(r: Float = 1f, g: Float = 0f, b: Float = 0f, a: Float = 1f) extends Comp {

  private def makeProp(name: Symbol, value: Float, color: Color): Property[Float] = {
    property(name, value)
            .onValueChange{ (o: Float, n: Float) => updateColor()}
            .editor(new SliderFactory(0f, 1f, backgroundPainter =
                new ColoredSliderBackgroundPainter(Color.WHITE, color)))
  }

  val red    = makeProp('red,   r, new Color(0.7f, 0.2f, 0.2f))
  val green  = makeProp('green, g, new Color(0.2f, 0.7f, 0.2f))
  val blue   = makeProp('blue,  b, new Color(0.2f, 0.2f, 0.7f))
  val alpha  = makeProp('alpha, a, new Color(0.5f, 0.5f, 0.5f))

  private var _color: Vec4 = Vec4(1,0,0,1)
  private var _intensity: Float = 1f

  updateColor()

  override protected def createCopy = new SolidColor()

  private def updateColor() {
    _color = Vec4(red(), green(), blue(), alpha())
    _intensity = (red() + green() + blue()) / 3f
  }

  override def intensity(pos: inVec2) = _intensity

  override def channel(channel: Symbol, pos: inVec2): Float = {
    channel match {
      case 'intensity => _intensity
      case 'red => _color.r
      case 'green => _color.g
      case 'blue => _color.b
      case 'alpha => _color.a
      case 'hue => ColorUtils.hue(_color)
      case 'saturation => ColorUtils.saturation(_color)
      case 'lightness => ColorUtils.lightness(_color)
      case _ => 0f
    }
  }

  def rgba(pos: inVec2) = _color
}