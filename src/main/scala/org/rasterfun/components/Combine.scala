package org.rasterfun.components

import org.rasterfun.component.Comp
import org.scalaprops.ui.editors.SelectionEditorFactory
import simplex3d.math.float.functions._
import simplex3d.math.float._

/**
 * Combines two components, using addition, multiplication, etc.
 */
class Combine extends Comp {

  val sourceA = addInput('a, new Noise(_seed = 43432))
  val sourceB = addInput('b, new Noise(_seed = 81324))

  val operation = p[CombineOp]('operation, MulOp).editor(new SelectionEditorFactory[CombineOp](List(
    AddOp,
    MulOp,
    SubOp,
    DivOp,
    AverageOp,
    MaxOp,
    MinOp,
    ModOp,
    PowOp
  )))

  override def channels = sourceA().channels union sourceB().channels

  def rgba(pos: inVec2) = operation().opColor(sourceA().rgba(pos),
                                              sourceB().rgba(pos))
  override def intensity(pos: inVec2) = operation().op(sourceA().intensity(pos),
                                                       sourceB().intensity(pos))
  override def channel(channel: Symbol, pos: inVec2) = operation().op(sourceA().channel(channel, pos),
                                                                      sourceB().channel(channel, pos))
}


abstract class CombineOp(name: String) {
  override def toString = name

  def op(a: Float, b: Float): Float
  def opColor(a: inVec4, b: inVec4): Vec4
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

case object AverageOp extends CombineOp("Average") {
  def op(a: Float, b: Float) = 0.5f * (a + b)
  def opColor(a: inVec4, b: inVec4) = 0.5f * (a + b)
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


