package org.rasterfun.components

import simplex3d.math._
import simplex3d.math.float._
import simplex3d.math.float.functions._


/**
 * Round rings.
 */
class Radial extends IntensityComp {

  val repeat = p('repeat, true)

  // TODO: Use enumeration with different shapes: Round, square, diamond..
  val square = p('square, false)

  protected def basicIntensity(pos: inVec2): Float = {

    val distance = if (square()) max(abs(pos.x), abs(pos.y))
                   else length(pos)

    val turns = if (repeat()) distance
                else clamp(distance, -0.5f, 0.5f)

    val Tau = 2 * Pi
    scala.math.cos(Tau * turns).toFloat
  }

}
