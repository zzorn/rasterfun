package org.rasterfun.components
import simplex3d.math._
import simplex3d.math.float._
import simplex3d.math.float.functions._


/**
 * Vertical or horizontal or angled stripes.
 */
class Stripes extends IntensityComp {

  val repeat = p('repeat, true)
//  val half = p('half, false) // TODO : Support half waves

  val vertical = p('vertical, false)

  // TODO: Use enumeration with different shapes: sine, square, triangle..
  val square = p('square, false)

  private val Tau = 2 * Pi

  protected def basicIntensity(pos: inVec2): Float = {

    val distance = if (vertical()) pos.x
                   else pos.y

    val turns = if (repeat()) distance
                else clamp(distance, -0.5f, 0.5f)

    if (square()) turns
    else scala.math.cos(Tau * turns).toFloat
  }

}
