package org.rasterfun.util

import simplex3d.math.float.functions._
import simplex3d.math.float._

/**
 * 
 */

object ColorUtils {

  val BLACK = Vec4(0f, 0f, 0f, 1f)
  val WHITE = Vec4(1f, 1f, 1f, 1f)
  val RED   = Vec4(1f, 0f, 0f, 1f)


  def hue(color: Vec4): Float = {
    val r = clampZeroOne(color.r)
    val g = clampZeroOne(color.g)
    val b = clampZeroOne(color.b)

    val max = math.max(math.max(r, g), b)
    val min = math.min(math.min(r, g), b)

    if (max == min) {
      // Greyscale
      0f
    }
    else {
      val d = max - min
      val h = if (max == r) (g - b) / d + (if (g < b) 6f else 0f)
        else if (max == g) (b - r) / d + 2f
        else if (max == b) (r - g) / d + 4f
        else 0f // NaN or similar

      h / 6f
    }
  }
  
  def saturation(color: Vec4): Float = {
    val r = clampZeroOne(color.r)
    val g = clampZeroOne(color.g)
    val b = clampZeroOne(color.b)

    val max = math.max(math.max(r, g), b)
    val min = math.min(math.min(r, g), b)

    val l = (max + min) / 2f

    if (max == min) {
      // Greyscale
      0f
    }
    else {
      val d = max - min
      if (l > 0.5f) d / (2f - max - min) else d / (max + min)
    }
  }

  def lightness(color: Vec4): Float = {
    val r = clampZeroOne(color.r)
    val g = clampZeroOne(color.g)
    val b = clampZeroOne(color.b)

    val max = math.max(math.max(r, g), b)
    val min = math.min(math.min(r, g), b)

    (max + min) / 2f
  }

  private def clampZeroOne(v: Float): Float = (if (v < 0f) 0f else if (v > 1f) 1f else v)
}