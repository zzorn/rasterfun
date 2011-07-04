package org.rasterfun.components

import org.rasterfun.component.Comp

import simplex3d.math._
import simplex3d.math.float._
import simplex3d.math.float.functions._
import java.util.Random

/**
 * Simplex Noise
 */
class Noise(_scale: Float = 1f,
            _detail: Int = 4,
            _x: Float = 0f, 
            _y: Float = 0f,
            _seed: Int = new Random().nextInt(1000),
            _amplitude: Float = 1f,
            _offset: Float = 0f) extends IntensityComp {

  private val maxDetail = 8

  val detail    = p('detail, _detail).translate((v:Int) => clamp(v, 1, maxDetail)) // = Turbulence
  val seed      = p('seed, _seed).onChange( {recalculateSeedOffsets()} )

  private var seedOffset: Vec2 = Vec2(0,0)

  offset := _offset
  amplitude := _amplitude

  recalculateSeedOffsets()

  private def recalculateSeedOffsets() {
    val r = new Random(seed())
    seedOffset.x = r.nextFloat * 1000f
    seedOffset.y = r.nextFloat * 1000f
  }

  override protected def createCopy = new Noise()

  protected def basicIntensity(pos: inVec2): Float = {

    val detailLevels = detail()
    val noiseVal: Float = if (detailLevels == 1) {
      // Simple noise, one octave
      val p = pos + seedOffset
      noise1(p)
    }
    else {
      // Turbulence noise made of several octaves
      var i = 0
      var noiseSum = 0f
      var amplitude = 1f
      var s: Float = 1f
      var offs = Vec2(seedOffset)
      while (i < detailLevels) {
        val p = pos * s + offs
        noiseSum += noise1(p) * amplitude
        s *= 2
        offs += s * i * 344.234f // Some offset to get different pattern for different octaves
        amplitude *= 0.5f
        i += 1
      }
      noiseSum
    }
    
    noiseVal
  }

}