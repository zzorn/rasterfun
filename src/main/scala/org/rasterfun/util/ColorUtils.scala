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


  /**
   *      Convert a Hue Saturation Lightness color to Red Green Blue color space.
   *      Algorithm based on the one in wikipedia ( http://en.wikipedia.org/wiki/HSL_color_space )
   */
  def HSLtoRGB(hue: Float, saturation: Float, lightness: Float, alpha: Float): Vec4 = {

    if (lightness == 0) {
      // Black
      Vec4(0,0,0,alpha)
    }
    else if (lightness == 1) {
      // White
      Vec4(1,1,1,alpha)
    }
    else if (saturation == 0) {
      // Grayscale
      Vec4(lightness,lightness,lightness,alpha)
    }
    else {
      // Arbitrary color

      def hueToColor(p: Float, q: Float, t: Float): Float = {
        var th = t
        if (th < 0) th += 1
        if (th > 1) th -= 1
        if (th < 1f / 6f) return p + (q - p) * 6f * th
        if (th < 1f / 2f) return q
        if (th < 2f / 3f) return p + (q - p) * (2f / 3f - th) * 6f
        return p
      }

      val q = if (lightness < 0.5f) (lightness * (1f + saturation)) else (lightness + saturation - lightness * saturation)
      val p = 2 * lightness - q;
      var r = hueToColor(p, q, hue + 1f / 3f)
      var g = hueToColor(p, q, hue)
      var b = hueToColor(p, q, hue - 1f / 3f)

      // Clamp
      if (r < 0f) r = 0f
      else if (r > 1f) r = 1f

      if (g < 0f) g = 0f
      else if (g > 1f) g = 1f

      if (b < 0f) b = 0f
      else if (b > 1f) b = 1f

      Vec4(r, g, b, alpha)
    }
  }


  private def clampZeroOne(v: Float): Float = (if (v < 0f) 0f else if (v > 1f) 1f else v)
}