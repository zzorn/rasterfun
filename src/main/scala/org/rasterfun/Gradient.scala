package org.rasterfun

import simplex3d.math.float._
import simplex3d.math.float.functions._

/**
 * 
 */
trait Gradient {

  def apply(v: Float): Vec4

}

object BlackWhiteGradient extends Gradient {
  def apply(v: Float): Vec4 = {
    val c = clamp(v, 0f, 1f)
    Vec4(c, c, c, 1f)
  }
}