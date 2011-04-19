package org.rasterfun.components

import org.rasterfun.component.Comp

import simplex3d.math._
import simplex3d.math.float._
import simplex3d.math.float.functions._
import org.rasterfun.util.ColorUtils

/**
 * 
 */
class Solid(r: Float = 1f, g: Float = 0f, b: Float = 0f, a: Float = 1f) extends Comp {

  val red    = p('red,   r).onValueChange{ (o: Float, n: Float) => updateColor()}
  val green  = p('green, g).onValueChange{ (o: Float, n: Float) => updateColor()}
  val blue   = p('blue,  b).onValueChange{ (o: Float, n: Float) => updateColor()}
  val alpha  = p('alpha, a).onValueChange{ (o: Float, n: Float) => updateColor()}

  private var _color: Vec4 = Vec4(1,0,0,1)
  private var _intensity: Float = 1f

  updateColor()

  override protected def createCopy = new Solid()

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