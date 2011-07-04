package org.rasterfun.components

import org.rasterfun.component.Comp
import simplex3d.math.float.functions._
import simplex3d.math.float._
import org.scalaprops.ui.editors.{SliderFactory, SelectionEditorFactory}
import org.scalaprops.ui.editors.SliderFactory._
import simplex3d.math.floatx.functions._

/**
 * Combines two components, using addition, multiplication, etc.
 */
class Combine extends Comp {

  val sourceA = addInput('a, new Noise(_seed = 43432))
  val sourceB = addInput('b, new Noise(_seed = 81324))
  val select  = addInput('select, new SolidIntensity(1f))

  val opacity = p('opacity, 1f).editor(new SliderFactory(0f, 1f))
  val swapInputs = p('swapInputs, false)

  val operation = p[CombineOp]('operation, MulOp).editor(new SelectionEditorFactory[CombineOp](List(
    SolidOp,
    AddOp,
    MulOp,
    SubOp,
    DivOp,
    MaxOp,
    MinOp,
    ModOp,
    PowOp
  )))

  override def channels = sourceA().channels union sourceB().channels

  private def combineColor(pos: inVec2, a: inVec4, b: inVec4): Vec4 = {
    val t = opacity() * select().intensity(pos)
    if (swapInputs()) mix(b, operation().opColor(b, a), t)
    else              mix(a, operation().opColor(a, b), t)
  }

  private def combineFloat(pos: inVec2, a: Float, b: Float): Float = {
    val t = opacity() * select().intensity(pos)
    if (swapInputs()) mix(b, operation().op(b, a), t)
    else              mix(a, operation().op(a, b), t)
  }

  def rgba(pos: inVec2) = combineColor(pos,
                                       sourceA().rgba(pos),
                                       sourceB().rgba(pos))

  override def intensity(pos: inVec2) = combineFloat(pos,
                                                     sourceA().intensity(pos),
                                                     sourceB().intensity(pos))

  override def channel(channel: Symbol, pos: inVec2) = combineFloat(pos,
                                                                    sourceA().channel(channel, pos),
                                                                    sourceB().channel(channel, pos))
}


abstract class CombineOp(name: String) {
  override def toString = name

  def op(a: Float, b: Float): Float
  def opColor(a: inVec4, b: inVec4): Vec4
}

case object SolidOp extends CombineOp("Solid") {
  def op(a: Float, b: Float) = b
  def opColor(a: inVec4, b: inVec4) = b
}

case object AddOp extends CombineOp("Add") {
  def op(a: Float, b: Float) = a + b
  def opColor(a: inVec4, b: inVec4) = a + b
}

case object MulOp extends CombineOp("Multiply") {
  def op(a: Float, b: Float) = a * b
  def opColor(a: inVec4, b: inVec4) = a * b
}

case object SubOp extends CombineOp("Subtract") {
  def op(a: Float, b: Float) = a - b
  def opColor(a: inVec4, b: inVec4) = a - b
}

case object DivOp extends CombineOp("Divide") {
  def op(a: Float, b: Float) = a / b
  def opColor(a: inVec4, b: inVec4) = a / b
}

case object MaxOp extends CombineOp("Max") {
  def op(a: Float, b: Float) = max(a, b)
  def opColor(a: inVec4, b: inVec4) = max(a, b)
}

case object MinOp extends CombineOp("Min") {
  def op(a: Float, b: Float) = min(a, b)
  def opColor(a: inVec4, b: inVec4) = min(a, b)
}

case object ModOp extends CombineOp("Modulus") {
  def op(a: Float, b: Float) = mod(a, b)
  def opColor(a: inVec4, b: inVec4) = mod(a, b)
}

case object PowOp extends CombineOp("To the power") {
  def op(a: Float, b: Float) = pow(a, b)
  def opColor(a: inVec4, b: inVec4) = pow(a, b)
}


