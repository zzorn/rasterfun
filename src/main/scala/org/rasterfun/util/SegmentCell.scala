package org.rasterfun.util

import simplex3d.math.float.functions._
import simplex3d.math.floatx.functions._
import java.util.Random

/**
 * A 1-dimensional subdivision of a line into separate segments / blocks, where the sizes can vary using a
 * random variable (and can be shifted?).
 */
final case class SegmentCell(var size: Float = 1, var sizeVariation: Float = 0) {

  private val random = new Random()

  var start: Float = 0f
  var end: Float = 1f
  var relativePosition: Float = 0.5f
  var cellId: Int = 0

  def center: Float = 0.5f * (start + end)

  def calculateCell(pos: Float, seed: Int): Float = {
    random.setSeed(seed)
    random.nextLong()
    random.nextLong()
    val seed2 = random.nextLong()

    def wallPos(cellId: Int): Float = {
      val basePos: Float = cellId * size
      if (sizeVariation == 0) basePos
      else {
        // Make a passable quality random number... TODO: Optimize
        random.setSeed(cellId ^ seed2)
        random.nextFloat()
        random.setSeed(random.nextLong())
        random.nextFloat()
        random.nextFloat()
        //basePos + 0.5f * size * clamp((random.nextGaussian() * sizeVariation).toFloat, -1f, 1f)
        basePos + 0.5f * size * sizeVariation * (random.nextFloat() * 2f - 1f)
      }
    }

    // Get cell according to non-randomized wall positions
    cellId = if (size == 0) 0 else (scala.math.floor(pos / size)).toInt

    // Get (randomized) wall positions
    start = wallPos(cellId)
    end = wallPos(cellId + 1)

    // Check if the point is actually in the next of previous cell
    if (pos < start) {
      cellId -= 1
      end = start
      start = wallPos(cellId)
    }
    else if (pos >= end) {
      cellId += 1
      start = end
      end = wallPos(cellId + 1)
    }

    // Get relative position
    relativePosition = if (start == end) 0.5f else (pos - start) / (end - start)

    relativePosition
  }

}