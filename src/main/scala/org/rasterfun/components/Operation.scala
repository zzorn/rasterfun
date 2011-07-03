package org.rasterfun.components

import org.scalaprops.ui.editors.SelectionEditorFactory
import simplex3d.math.float.functions._
import simplex3d.math.float._
import org.rasterfun.component.Comp

/**
 * Perform some simple mathematical operation on the source component output.
 * E.g. absolute value, negate, square root, logarithm, sin, cos, etc.
 */
class Operation extends IntensityComp {

  val source = addInput('a, new Noise(_seed = 43432))

  val operation = p[UnaryOp]('operation, RoundOp).editor(new SelectionEditorFactory[UnaryOp](List(
    NegOp, AbsOp, SignOp, RoundOp,
    SqrtOp, SqrOp,
    SinOp, CosOp, TanOp,
    ASinOp, ACosOp, ATanOp,
    SinHOp, CosHOp, TanHOp,
    LogOp, Log2Op,
    ExpOp, Exp2Op
  )))

  override def channels = source().channels

  protected def basicIntensity(pos: inVec2) = (operation().op(source().intensity(pos))) * 2f - 1f

  override def nonStandardChannel(channel: Symbol, pos: inVec2) = operation().op(source().channel(channel, pos))

}

abstract class UnaryOp(name: String) {
  override def toString = name

  def op(v: Float): Float
  def opColor(v: inVec4): Vec4
}

case object NegOp extends UnaryOp("Negate") {
  def op(v: Float) = -v
  def opColor(v: inVec4) = -v
}

case object AbsOp extends UnaryOp("Absolute value") {
  def op(v: Float) = abs(v)
  def opColor(v: inVec4) = abs(v)
}

case object SignOp extends UnaryOp("Sign") {
  def op(v: Float) = sign(v)
  def opColor(v: inVec4) = sign(v)
}


case object RoundOp extends UnaryOp("Round") {
  def op(v: Float) = round(v)
  def opColor(v: inVec4) = round(v)
}


case object SqrtOp extends UnaryOp("Square root") {
  def op(v: Float) = sqrt(v)
  def opColor(v: inVec4) = sqrt(v)
}

case object SqrOp extends UnaryOp("Square") {
  def op(v: Float) = v * v
  def opColor(v: inVec4) = v * v
}


case object SinOp extends UnaryOp("Sine") {
  def op(v: Float) = sin(v)
  def opColor(v: inVec4) = sin(v)
}

case object CosOp extends UnaryOp("Cosine") {
  def op(v: Float) = cos(v)
  def opColor(v: inVec4) = cos(v)
}

case object TanOp extends UnaryOp("Tangent") {
  def op(v: Float) = tan(v)
  def opColor(v: inVec4) = tan(v)
}

case object ASinOp extends UnaryOp("Arc Sine") {
  def op(v: Float) = asin(v)
  def opColor(v: inVec4) = asin(v)
}

case object ACosOp extends UnaryOp("Arc Cosine") {
  def op(v: Float) = acos(v)
  def opColor(v: inVec4) = acos(v)
}

case object ATanOp extends UnaryOp("Arc Tangent") {
  def op(v: Float) = atan(v)
  def opColor(v: inVec4) = atan(v)
}

case object SinHOp extends UnaryOp("Hyperbolic Sine") {
  def op(v: Float) = sinh(v)
  def opColor(v: inVec4) = sinh(v)
}

case object CosHOp extends UnaryOp("Hyperbolic Cosine") {
  def op(v: Float) = cosh(v)
  def opColor(v: inVec4) = cosh(v)
}

case object TanHOp extends UnaryOp("Hyperbolic Tangent") {
  def op(v: Float) = tanh(v)
  def opColor(v: inVec4) = tanh(v)
}


case object LogOp extends UnaryOp("Natural Logarithm") {
  def op(v: Float) = log(v)
  def opColor(v: inVec4) = log(v)
}

case object Log2Op extends UnaryOp("Binary logarithm") {
  def op(v: Float) = log2(v)
  def opColor(v: inVec4) = log2(v)
}

case object ExpOp extends UnaryOp("Exp") {
  def op(v: Float) = exp(v)
  def opColor(v: inVec4) = exp(v)
}

case object Exp2Op extends UnaryOp("Exp2") {
  def op(v: Float) = exp2(v)
  def opColor(v: inVec4) = exp2(v)
}

