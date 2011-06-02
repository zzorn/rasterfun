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

object IntensityGradient extends Gradient {
  def apply(v: Float): Vec4 = {
    // White for values in range 0..1
    if (v >= 0) {
      if (v <= 1) Vec4(v, v, v, 1f)
      else {
        // Orange for higher values
        val c = 1f / (v*0.5f + 0.5f)
        val c2 = 1f / (v*v)
        Vec4(1f, c, c2, 1f)
      }
    }
    else {
      // Blue for negative values in range -1..0
      if (v >= -1) Vec4(0f, 0f, -v, 1f)
      else {
        // Cyan for lower values
        val c = 1f + 1f / v
        Vec4(0f, c, 1f, 1f)
      }
    }
  }
}