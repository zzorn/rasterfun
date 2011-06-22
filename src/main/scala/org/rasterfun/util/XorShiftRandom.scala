package org.rasterfun.util

import java.util.Random


/**
 * XorShift implementation of Random.
 *
 * NOTE: Not thread safe, and doesn't serialize it's seed.
 *
 * Implementation of the XorShift algorithm, see
 *   http://www.jstatsoft.org/v08/i14/paper
 *   http://en.wikipedia.org/wiki/Xorshift
 *
 * TODO: DOESN'T WORK, RESULT IS HIGHLY PERIODIC!
 */
final class XorShiftRandom extends Random  {

  private var s1: Int = XorShiftConstants.DEFAULT_SEED_1
  private var s2: Int = XorShiftConstants.DEFAULT_SEED_2
  private var s3: Int = XorShiftConstants.DEFAULT_SEED_3
  private var s4: Int = XorShiftConstants.DEFAULT_SEED_4

  setSeed(
    System.nanoTime.toInt ^ XorShiftConstants.DEFAULT_SEED_1,
    System.nanoTime.toInt ^ XorShiftConstants.DEFAULT_SEED_2,
    System.nanoTime.toInt ^ XorShiftConstants.DEFAULT_SEED_3,
    System.nanoTime.toInt ^ XorShiftConstants.DEFAULT_SEED_4
  )

  override def setSeed(seed: Long) {
    setSeed((seed & 0xffffffff).toInt,
            ((seed >>> 32) & 0xffffffff).toInt)
  }

  /**
   * Reseeds the random number sequence with the specified seeds.
   * If a seed is 0 the default value is used instead.
   */
  def setSeed(seed1: Int,
              seed2: Int,
              seed3: Int = XorShiftConstants.DEFAULT_SEED_3,
              seed4: Int = XorShiftConstants.DEFAULT_SEED_4) {

    // Call java random set seed to have it reset its have next gaussian field
    super.setSeed(seed1)

    val ssd1 = XorShiftConstants.DEFAULT_SEED_1 ^ seed1
    val ssd2 = XorShiftConstants.DEFAULT_SEED_2 ^ seed2
    val ssd3 = XorShiftConstants.DEFAULT_SEED_3 ^ seed3
    val ssd4 = XorShiftConstants.DEFAULT_SEED_4 ^ seed4

    // Zero seeds can not be used, if any is zero use the default one instead.
    s1 = if (ssd1 != 0) ssd1 else XorShiftConstants.DEFAULT_SEED_1
    s2 = if (ssd2 != 0) ssd2 else XorShiftConstants.DEFAULT_SEED_2
    s3 = if (ssd3 != 0) ssd3 else XorShiftConstants.DEFAULT_SEED_3
    s4 = if (ssd4 != 0) ssd4 else XorShiftConstants.DEFAULT_SEED_4

    // Cycle in the seeds
    nextInt()
    nextInt()
    nextInt()
    nextInt()

  }


  override def nextInt(): Int = {
    val t = s1 ^ (s1 << 11)
    s1 = s2
    s2 = s3
    s3 = s4
    s4 = s4 ^ (s4 >>> 19) ^ (t ^ (t >>> 8))
    s4
  }

  override def next(bits: Int): Int = {
    // Mask out the higher bits (required by other methods in Random)
    nextInt() & ((1 << bits) - 1)
  }

  /** A float value from min (exclusive) to max (inclusive) */
  def nextFloatInRange(min: Float, max: Float): Float = min + nextFloat * (max - min)

  /**
   * A poisson number with specified mean
   */
  def poisson(mean: Float): Int = {
    val g = math.exp(-mean).toFloat
    var em = 0
    var t = nextFloat
    while (t > g) {
      em += 1
      t *= nextFloat
    }
    return em
  }

}

private object XorShiftConstants {
  val DEFAULT_SEED_1 = 123456789
  val DEFAULT_SEED_2 = 362436069
  val DEFAULT_SEED_3 = 521288629
  val DEFAULT_SEED_4 = 88675123
}

