package org.rasterfun.components

import org.rasterfun.component.Comp
import simplex3d.math._
import simplex3d.math.float._
import simplex3d.math.float.functions._
import sun.net.www.content.audio.basic
import org.scalaprops.ui.editors.SliderFactory._
import org.scalaprops.ui.editors.{ColoredSliderBackgroundPainter, SliderFactory}
import org.rasterfun.util.gradient._

/**
 * Provides some functionality common to all components that provide just an intensity channel.
 */
// TODO: Add rotation also?
// TODO: Move gradient to own component, to make intensity components a bit more straightforward?
trait IntensityComp extends Comp {

  val gradient  = p[Gradient]('gradient, DefaultGradients.Intensity).editor(GradientEditorFactory)

  val amplitude = p('amplitude, 1f).editor(new SliderFactory(-3f, 3f,restrictNumberFieldMax = false,restrictNumberFieldMin = false))
  val offset     = p('offset, 0f).editor(new SliderFactory(-1f, 1f,restrictNumberFieldMax = false,restrictNumberFieldMin = false))

  val scale    = p('scale, 1f).editor(new SliderFactory(0f, 4f,restrictNumberFieldMax = false,restrictNumberFieldMin = false)) onChange(updateScale())
  val stretch  = p('stretch, 0f).editor(new SliderFactory(-4f, 4f,restrictNumberFieldMax = false,restrictNumberFieldMin = false)) onChange(updateScale())
  val xDelta   = p('xDelta, 0f).editor(new SliderFactory(-1f, 1f,restrictNumberFieldMax = false,restrictNumberFieldMin = false)) onChange(updateOffset())
  val yDelta   = p('yDelta, 0f).editor(new SliderFactory(-1f, 1f,restrictNumberFieldMax = false,restrictNumberFieldMin = false)) onChange(updateOffset())

  // TODO: Enum with alternatives no boundaries, clamp, wrap, (and zig zag wrap etc?)
  val clampResult = p('clamp, true)
  val wrapResult = p('wrap, false)

  val centerOnZero = p('centerOnZero, false)
  val invert = p('invert, false)

  private var posOffset: Vec2 = Vec2(0f, 0f)
  private var posScale: Vec2 = Vec2(1f, 1f)

  private def updateOffset() {
    posOffset = Vec2(xDelta(),
                     yDelta())
  }

  private def updateScale() {
    val xs = max(1f, 1f - stretch())
    val ys = max(1f, 1f + stretch())
    posScale = Vec2(scale() * xs,
                    scale() * ys)
  }

  /**
   * Returns a value between -1 and 1 (can also be under or over).
   */
  protected def basicIntensity(pos: inVec2): Float

  def projectPos(pos: inVec2): Vec2 = {
    ( pos + posOffset ) * posScale
  }

  def pureIntensity(pos: inVec2): Float = {
    var v = basicIntensity(projectPos(pos))

    val minusOneToOne = centerOnZero()
    if (!minusOneToOne) {
      // Scale from -1..1 to 0..1
      v = (v + 1f) * 0.5f
    }

    // Scale and offset
    v = v * amplitude() + offset()

    if (wrapResult()) {
      if (v < -1 || v > 1) v = v % 1f
      if (!minusOneToOne && v < 0) v = 1f - v
    }
    else if (clampResult()) {
      if (minusOneToOne) v = clamp(v, -1f, 1f)
      else v = clamp(v, 0f, 1f)
    }

    if (invert()) {
      if (minusOneToOne) v = -v
      else v = 1f - v // Center on 0.5f
    }

    v
  }


  override def intensity(pos: inVec2): Float = pureIntensity(pos)

  override def rgba(pos: inVec2): Vec4 = gradient()(pureIntensity(pos))

}