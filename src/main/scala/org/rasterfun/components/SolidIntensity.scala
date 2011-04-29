package org.rasterfun.components

import org.rasterfun.component.Comp
import simplex3d.math._
import simplex3d.math.float._
import simplex3d.math.float.functions._
import org.scalaprops.Property
import org.scalaprops.ui.editors.SliderFactory._
import org.scalaprops.ui.editors.{ColoredSliderBackgroundPainter, SliderFactory}

/**
 * A simple component with one intensity value.
 * Used e.g. as input placeholder for basic components.
 */
class SolidIntensity(_value: Float = 0.5f) extends Comp {

  val value  = p('value, _value)
          .onChange (updateColor())
          .editor(new SliderFactory(0f,
                                    1f,
                                    restrictNumberFieldMax = false,
                                    restrictNumberFieldMin = false))

  private var _color: Vec4 = Vec4(0,0,0,1)
  private var _intensity: Float = 1f

  updateColor()

  override protected def createCopy = new SolidIntensity(value())

  private def updateColor() {
    _color = Vec4(value(), value(), value(), 1f)
    _intensity = value()
  }

  override def intensity(pos: inVec2) = _intensity

  override def channel(channel: Symbol, pos: inVec2): Float = value()

  def rgba(pos: inVec2) = _color
}