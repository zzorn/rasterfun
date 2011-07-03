package org.rasterfun.components

import org.rasterfun.component.Comp
import simplex3d.math.float.functions._
import simplex3d.math.float._
import org.rasterfun.util.ColorUtils
import org.scalaprops.ui.editors.SliderFactory

/**
 * Adjusts the hue, saturation and/or lightness of a source component.
 */
class HslAdjust extends Comp {

  val source     = addInput('source, new Noise(_seed = 126326373))

  val hueAdjust = p('hueAdjust, 0f).editor(new SliderFactory[Float](-0.5f, 0.5f))
  val satAdjust = p('satAdjust, 0.5f).editor(new SliderFactory[Float](-1f, 1f))
  val lumAdjust = p('lumAdjust, 0f).editor(new SliderFactory[Float](-1f, 1f))

  override def channels = source().channels

  def rgba(pos: inVec2) = {
    ColorUtils.adjustColorHSL(source().rgba(pos), hueAdjust(), satAdjust(), lumAdjust())
  }

  override def nonStandardChannel(channel: Symbol, pos: inVec2) = source().channel(channel, pos)

}
