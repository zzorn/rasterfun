package org.rasterfun.util

import math._

import org.rasterfun.util.MathUtils._

/**
 * Noise based on adding together noise kernels from current and neighboring grid cells.
 * See http://graphics.cs.kuleuven.be/publications/LLDD09PNSGC/
 */
// TODO: Different kernels, for isotropic etc noises.
final case class GaborNoise(K: Float = 1f,
                            a: Float = 0.05f,
                            F0: Float = 0.0625f,
                            omega0: Float = TauF / 8,
                            impulsesPerKernel: Int = 64,
                            randomSeed: Int = System.nanoTime.toInt,
                            period: Int = 1 << 16) {

  private val rng = new XorShiftRandom()
  private val cutOffDistance = 0.05

  val kernelRadius: Float = (sqrt(-log(cutOffDistance) / Pi) / a).toFloat
  val impulseDensity: Float = (impulsesPerKernel / (Pi * kernelRadius*kernelRadius)).toFloat

  def variance: Float = {
    val integralGaborFilterSquared = ((K*K) / (4f * a*a)) * (1.0 + exp(-(Tau * F0*F0) / (a*a))).toFloat
    impulseDensity * integralGaborFilterSquared / 3f
  }

  def noise1(x: Float, y: Float): Float = {
    val xp = x / kernelRadius
    val yp = y / kernelRadius
    val xInt = floor(xp).toInt
    val yInt = floor(yp).toInt
    val xFrac = xp - xInt
    val yFrac = yp - yInt

    var noise = 0f

    var di = -1
    while (di <= 1) {
      var dj = -1
      while (dj <= 1) {
        noise += cell(xInt + di, xInt + dj, xFrac - di, yFrac - dj)

        dj += 1
      }
      di += 1
    }

    noise
  }

  private def cell(cellX: Int, cellY: Int, x: Float, y: Float): Float = {

    // NOTE: Can be optimized for 2^n periods by using masks instead of mod operations
    rng.setSeed(randomSeed, cellX % period, cellY % period)

    val numberOfImpulsesPerCell = impulseDensity * kernelRadius*kernelRadius
    val numberOfImpulses = rng.poisson(numberOfImpulsesPerCell)

    var noise = 0f

    var i = 0
    while (i < numberOfImpulses) {
      val xi = rng.nextFloat
      val yi = rng.nextFloat
      val wi = rng.nextFloatInRange(-1, 1)
      // val omega0i = rng.uniform(0, TauF) // Needed for isotropic

      val dx = x - xi
      val dy = y - yi
      if ((dx*dx + dy*dy) < 1f) {
        noise += wi * gabor(omega0, dx*kernelRadius, dy*kernelRadius) // Anisotropic (=directed)
        // noise += wi * gabor(omega0i, dx*kernelRadius, dy*kernelRadius) // Isotropic (=same in all directions)
      }

      i += 1
    }

    noise
  }

  private def gabor(omega0: Float, x: Float, y: Float): Float = {
    val gaussianEnvelope = K * exp(-HalfTau * (a*a) * ((x*x) + (y*y)))
    val sinusoidalCarrier = cos(Tau * F0 * ((x * cos(omega0)) + (y * sin(omega0))))
    (gaussianEnvelope * sinusoidalCarrier).toFloat
  }

}