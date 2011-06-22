package org.rasterfun.util

/**
 * Fast XorShift RNG.
 *
 * NOTE: Never returns zero from nextInt
 *
 * TODO: DOESN'T WORK, HIGHLY PERIODIC!
 */
final class FastXorShift {
  private var x: Int = (System.nanoTime & 0xffffffff).toInt
  if (x == 0) x = 362436069

  def setSeed(seed: Int) {
    x = if (seed != 0) seed else 362436069
  }

  def setSeed(seed1: Int, seed2: Int) {
    setSeed(seed1 ^ (seed2 << 16) ^ (seed2 >>> 16))
  }


  /** A random integer not equal to zero. */
  def nextInt(): Int = {
    x ^= (x << 13)
    x ^= (x >>> 17)
    x ^= (x << 5)
    x
  }

  /** A value from 0 (exclusive) to 1 (inclusive) */
  def nextFloat(): Float = nextInt().toFloat / Int.MaxValue.toFloat
  
  /** A value from min (exclusive) to max (inclusive) */
  def nextFloatInRange(min: Float, max: Float): Float = min + nextFloat() * (max - min)

  def poisson(mean: Float): Float = {
    val g = math.exp(-mean).toFloat
    var em = 0
    var t = nextFloat()
    while (t > g) {
      em += 1
      t *= nextFloat()
    }
    return em
  }
}