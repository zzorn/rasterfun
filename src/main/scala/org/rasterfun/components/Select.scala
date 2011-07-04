package org.rasterfun.components

import simplex3d.math._
import simplex3d.math.float._
import simplex3d.math.float.functions._
import org.scalaprops.ui.editors.SliderFactory._
import org.scalaprops.ui.editors.{SelectionEditorFactory, SliderFactory}
import org.rasterfun.util.curve.{SmoothProjection, Projection}

/**
 * Selects a value from an input heightmap, and returns 1 close to the value, falling to 0 with distance.
 */
class Select extends IntensityComp {

  private def makeDefaultSource: Radial = {
    val r = new Radial()
    r.repeat := false
    r.scale := 0.5f
    r
  }

  val source = addInput('source, makeDefaultSource)

  val threshold = p('threshold, 0.5f).editor(new SliderFactory(0f, 1f,restrictNumberFieldMax = false,restrictNumberFieldMin = false))

  val downCurve  = p[Projection]('downCurve,   SmoothProjection).editor(new SelectionEditorFactory[Projection](Projection.standardProjections))
  val upCurve    = p[Projection]('upCurve, SmoothProjection).editor(new SelectionEditorFactory[Projection](Projection.standardProjections))

  val width      = p('width, 0.1f).editor(new SliderFactory(0f, 1f,restrictNumberFieldMax = false,restrictNumberFieldMin = true))
  val downExtend = p('downExtend, 0.4f).editor(new SliderFactory(0f, 1f,restrictNumberFieldMax = false,restrictNumberFieldMin = true))
  val upExtend   = p('upExtend, 0.4f).editor(new SliderFactory(0f, 1f,restrictNumberFieldMax = false,restrictNumberFieldMin = true))

  protected def basicIntensity(pos: inVec2) = {
    val v: Float = source().intensity(pos)
    val halfWidth: Float = width() * 0.5f
    val topStart = threshold() - halfWidth
    val topEnd   = threshold() + halfWidth

    val result =
      if      (v >= topEnd)  upCurve()(1f - (v - topEnd) / upExtend())
      else if (v < topStart) downCurve()(1f - (topStart - v) / downExtend())
      else 1.0f
    
    result * 2f - 1f
  }


}