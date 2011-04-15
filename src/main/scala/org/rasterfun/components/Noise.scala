package org.rasterfun.components

import org.rasterfun.component.Comp

import simplex3d.math._
import simplex3d.math.float._
import simplex3d.math.float.functions._
import org.rasterfun.BlackWhiteGradient

/**
 * 
 */
class Noise(_scale: Float = 1f, _detail: Int = 4, _x: Float = 0f, _y: Float = 0f) extends Comp {

  private val maxDetail = 8

  val detail   = p('detail, _detail).translate((v:Int) => clamp(v, 1, maxDetail)) // = Turbulence
  val scale    = p('scale, _scale)
  val offset   = p('offset, Vec2(_x, _y))
  val gradient = p('gradient, BlackWhiteGradient)

  override def intensity(pos: inVec2): Float = {

    val detailLevels = detail()
    val noiseVal: Float = if (detailLevels == 1) {
      // Simple noise, one octave
      noise1(pos * scale() + offset())
    }
    else {
      // Turbulence noise made of several octaves
      var i = 0
      var noiseSum = 0f
      var amplitude = 1f
      var s: Float = scale()
      var offs = Vec2(offset())
      while (i < detailLevels) {
        noiseSum += noise1(pos * s + offs) * amplitude
        s *= 2
        offs += s * i * 344.234f // Some offset to get different pattern for different octaves
        amplitude *= 0.5f
        i += 1
      }
      noiseSum
    }
    
    noiseVal * 0.5f + 0.5f // Scale to 0..1 range from -1..1
  }

  def rgba(pos: inVec2): Vec4 = {
    gradient()(intensity(pos)) // Get gradient value
  }
}