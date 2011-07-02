package org.rasterfun.util.gradient

import java.util.ArrayList

import simplex3d.math.float._
import simplex3d.math.float.functions._
import java.awt.Color
import org.rasterfun.util.ColorUtils

/**
 * Gradient consisting of points with values.
 * The parameter, if specified, should be a _sorted_ set of control points (use .sorted method to sort them before calling if necessary)
 */
class Gradient(initialPoints: List[GradientPoint] = Nil) extends AbstractGradient {

  val controlPoints: List[GradientPoint] = initialPoints.sortWith(_ < _)

  def defaultColor: Vec4 = Vec4(0,0,0,1)

  def apply(v: Float): Vec4 = {
    var cs = controlPoints
    var prev: GradientPoint = null
    var next: GradientPoint = if (cs != Nil) cs.head else null

    while (cs != Nil && v > cs.head.value) {
      cs = cs.tail
      prev = next
      next = if (cs != Nil) cs.head else null
    }

    if (prev == null && next == null) defaultColor
    else if (prev == null) next.color
    else if (next == null) prev.color
    else {
      val range: Float =  next.value - prev.value
      val r = if (range == 0) 0.5f else (v - prev.value) / range
      mix(prev.color, next.color, r)
    }
  }

  def intensity(v: Float): Float = {
    val c = apply(v)
    (c.r + c.g + c.b) / 3f
  }

  /** Adds control point */
  def +(c: GradientPoint): Gradient = {
    var sortedTail = controlPoints
    var sortedHead: List[GradientPoint] = Nil

    while (sortedTail != Nil && sortedTail.head.value < c.value) {
      sortedHead ::= sortedTail.head
      sortedTail = sortedTail.tail
    }

    new Gradient(sortedHead.reverse ::: List(c) ::: sortedTail )
  }
  
  /** Removes control point */
  def -(c: GradientPoint): Gradient = {
    new Gradient(controlPoints.filterNot(_ == c))
  }

  def getClosestPoint(pos: Float): GradientPoint = {
    var closestPoint : GradientPoint = null

    controlPoints.foreach{ c =>
      if (closestPoint == null) closestPoint = c
      else if (abs(c.value - pos) < abs(closestPoint.value - pos)) closestPoint = c
    }

    closestPoint
  }

}

case class GradientPoint(value: Float, color: Vec4) extends Comparable[GradientPoint] {

  def compareTo(o: GradientPoint): Int = {
    if (value < o.value) -1
    else if (value > o.value) 1
    else 0
  }

  def < (o: GradientPoint): Boolean = value < o.value

  def solidJavaColor: Color = new Color(color.r, color.g, color.b)

  /**
   * Returns a new instance with a new value.
   */
  def newValue(v: Float): GradientPoint = {
    if (v == value) this
    else GradientPoint(v, color)
  }

  /**
   * Returns a new instance with a new color.
   */
  def newAdjustedHue(hueAdj: Float): GradientPoint = {
    GradientPoint(value, ColorUtils.adjustColorHSL(color, hueDelta = hueAdj))
  }

  /**
   * Returns a new instance with a new color.
   */
  def newAdjustedLum(lumAdj: Float): GradientPoint = {
    GradientPoint(value, ColorUtils.adjustColorHSL(color, lumDelta = lumAdj))
  }

  /**
   * Returns a new instance with a new color.
   */
  def newAdjustedSat(satAdj: Float): GradientPoint = {
    GradientPoint(value, ColorUtils.adjustColorHSL(color, satDelta = satAdj))
  }

  /**
   * Returns a new instance with a new color.
   */
  def newAdjustedAlpha(alphaAdj: Float): GradientPoint = {
    GradientPoint(value, ColorUtils.adjustColorHSL(color, alphaDelta = alphaAdj))
  }
}
