package org.rasterfun.components

import simplex3d.math._
import simplex3d.math.float._
import simplex3d.math.float.functions._
import org.rasterfun.component.Comp
import org.rasterfun.util.ColorUtils
import java.awt.Color

/**
 * Convert a hue, saturation, and lightness input into a color.
 */
class HslToColor(_hue: Comp        = new Noise(_amplitude = 0.1f, _seed= 2134),
                 _saturation: Comp = new Noise(_amplitude = 0.2f, _offset = 0.5f, _seed= 8769),
                 _lightness: Comp  = new Noise(_amplitude = 0.2f, _offset = 0.5f, _seed= 5632)) extends Comp {

  val hue         = addInput('hue, _hue)
  val saturation  = addInput('saturation, _saturation)
  val lightness   = addInput('lightness, _lightness)

  override protected def createCopy = new HslToColor()

  def rgba(pos: inVec2): Vec4 = {

    val h = hue().intensity(pos)
    val s = saturation().intensity(pos)
    val l = lightness().intensity(pos)

    ColorUtils.HSLtoRGB(h, s, l, 1f)
  }

}