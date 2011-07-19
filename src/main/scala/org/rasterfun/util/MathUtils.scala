package org.rasterfun.util

import simplex3d.math.float.functions._
import simplex3d.math.float._
import java.lang.Math

/**
 * 
 */
object MathUtils {

  val Tau = 2.0 * math.Pi
  val HalfTau = math.Pi

  val TauF = Tau.toFloat
  val HalfTauF = HalfTau.toFloat



  def distanceAlongLine(point:inVec2, a: inVec2, delta: inVec2): Float = {

    if (delta.x == 0 && delta.y == 0) 0.5f
    else {
      ( (point.x - a.x) * delta.x +
        (point.y - a.y) * delta.y ) /
      (delta.x * delta.x + delta.y * delta.y)
    }
  }

  def closestPointOnLineSegment(point:inVec2, a: inVec2, b: inVec2): Vec2 = {

    val delta = b - a

    if (delta.x == 0 && delta.y == 0) a
    else {
      val t = distanceAlongLine(point, a, delta)

      if (t <= 0)  a
      else if (t >= 1) b
      else a + t * delta
    }
  }

  def distanceToLineSegment(point:inVec2, a: inVec2, b: inVec2): Float = {
    // TODO: Negate distance if point is on left of line
    distance(closestPointOnLineSegment(point, a, b), point)
  }

  def directionOfVectorInTurns(v: inVec2): Float = {
    if (v == Vec2.Zero) 0f
    else atan(v.y, v.x) / Tau.toFloat
  }
}
