package org.rasterfun.util.gradient

import java.util.ArrayList

import simplex3d.math.float._
import simplex3d.math.float.functions._
import com.sun.org.apache.xerces.internal.impl.xs.util.NSItemListImpl

/**
 * Gradient consisting of points with values.
 * The parameter, if specified, should be a _sorted_ set of control points (use .sorted method to sort them before calling if necessary)
 */
class IntervalGradient(controlPoints: List[GradientPoint] = Nil) extends Gradient {

  def defaultColor: Vec4 = Vec4(0,0,0,1)

  def apply(v: Float): Vec4 = {
    var cs = controlPoints
    var prev: GradientPoint = null
    var next: GradientPoint = null

    while (cs != Nil && cs.head.value < v) {
      prev = next
      next = cs.head
      cs = cs.tail
    }

    if (prev == null && next == null) defaultColor
    else if (prev == null) next.color
    else if (next == null) prev.color
    else {
      val range = next.value - prev.value
      val r = if (range == 0) 0.5f else (v - prev.value) / range
      mix(prev.color, next.color, r)
    }
  }

  /** Adds control point */
  def +(c: GradientPoint): IntervalGradient = {
    var sortedTail = controlPoints
    var sortedHead: List[GradientPoint] = Nil

    while (sortedTail != Nil && sortedTail.head.value < c.value) {
      sortedHead ::= sortedTail.head
      sortedTail = sortedTail.tail
    }

    new IntervalGradient(sortedHead.reverse ::: List(c) ::: sortedTail )
  }
  
  /** Removes control point */
  def -(c: GradientPoint): IntervalGradient = {
    new IntervalGradient(controlPoints.filterNot(_ == c))
  }

}

case class GradientPoint(value: Float, color: Vec4) extends Comparable[GradientPoint] {
  def compareTo(o: GradientPoint): Int = {
    if (value < o.value) -1
    else if (value > o.value) 1
    else 0
  }
}
