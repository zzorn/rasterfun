package org.rasterfun.components

import org.rasterfun.component.Comp

import simplex3d.math._
import simplex3d.math.float._
import simplex3d.math.float.functions._
import java.util.Random
import org.scalaprops.ui.editors.{NumberEditorFactory, SliderFactory}
import org.rasterfun.util.GaborNoise
import org.rasterfun.{Gradient, BlackWhiteGradient}

/**
 * 
 */
class Noise(_scale: Float = 1f, _detail: Int = 4, _x: Float = 0f, _y: Float = 0f, _seed: Int = new Random().nextInt(1000)) extends Comp {

  private val maxDetail = 8

  val detail    = p('detail, _detail).translate((v:Int) => clamp(v, 1, maxDetail)) // = Turbulence
  val scale     = p('scale, _scale).editor(new SliderFactory(0f, 5f,restrictNumberFieldMax = false))
  val amplitude = p('amplitude, 1f).editor(new SliderFactory(-1f, 1f,restrictNumberFieldMax = false,restrictNumberFieldMin = false))
  val offset    = p('offset, 0f).editor(new SliderFactory(0f, 1f,restrictNumberFieldMax = false,restrictNumberFieldMin = false))
  val seed      = p('seed, _seed).onChange( {recalculateSeedOffsets()} )
  val gradient  = p[Gradient]('gradient, BlackWhiteGradient)

  private var seedOffset: Vec2 = Vec2(0,0)

  recalculateSeedOffsets()

  private def recalculateSeedOffsets() {
    val r = new Random(seed())
    seedOffset.x = r.nextFloat * 1000f
    seedOffset.y = r.nextFloat * 1000f
  }

  override protected def createCopy = new Noise()

  override def intensity(pos: inVec2): Float = {

    val detailLevels = detail()
    val noiseVal: Float = if (detailLevels == 1) {
      // Simple noise, one octave
      val p = pos * scale() + seedOffset
      noise1(p)
    }
    else {
      // Turbulence noise made of several octaves
      var i = 0
      var noiseSum = 0f
      var amplitude = 1f
      var s: Float = scale()
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
    
    offset() + amplitude() * (noiseVal * 0.5f + 0.5f) // Scale to 0..1 range from -1..1
  }

  def rgba(pos: inVec2): Vec4 = {
    gradient()(intensity(pos)) // Get gradient value
  }
}