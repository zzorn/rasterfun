package org.rasterfun.components

import simplex3d.math._
import simplex3d.math.float._
import simplex3d.math.float.functions._


import org.rasterfun.component.Comp
import org.scalaprops.ui.editors.SliderFactory
import org.scalaprops.ui.editors.SliderFactory._
import org.rasterfun.{Gradient, BlackWhiteGradient}

/**
 * 
 */
class Radial extends Comp {

  val gradient  = p[Gradient]('gradient, BlackWhiteGradient)
  val scale     = p('scale, 1f).editor(new SliderFactory(0f, 10f,restrictNumberFieldMax = false))
  val amplitude = p('amplitude, 1f).editor(new SliderFactory(-1f, 1f,restrictNumberFieldMax = false,restrictNumberFieldMin = false))
  val offset    = p('offset, 0f).editor(new SliderFactory(0f, 1f,restrictNumberFieldMax = false,restrictNumberFieldMin = false))
  val centerX   = p('centerX, 0f).editor(new SliderFactory(-1f, 1f,restrictNumberFieldMax = false,restrictNumberFieldMin = false))
  val centerY   = p('centerY, 0f).editor(new SliderFactory(-1f, 1f,restrictNumberFieldMax = false,restrictNumberFieldMin = false))

  override def intensity(pos: inVec2): Float = {
    val center = Vec2(centerX(), centerY())
    val d = distance(pos, center)
    offset() + amplitude() * (0.5f + 0.5f * scala.math.cos(2*Pi * d * scale()).toFloat)
  }

  def rgba(pos: inVec2) = gradient()(intensity(pos))


}