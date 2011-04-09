package org.rasterfun.raster

import org.rasterfun.functions.{Fun}
import org.rasterfun.{Area}
import org.rasterfun.functions.Fun

/**
 * 
 */
class RasterChannel(val width: Int, val height: Int) {

  def this(width: Int, height: Int, sourceField: Fun, area: Area) {
    this(width, height)
    setFrom(sourceField, area)
  }

  val size = width * height
  val data: Array[Float] = Array[Float](size)

  def set(x: Int, y: Int, value: Float) {
    if (isInside(x, y)) data(x + y * width) = value
  }

  def apply(x: Int, y: Int): Float = {
    if (isInside(x, y)) data(x + y * width)
    else 0f
  }

  def setFrom(field: Fun, area: Area) {
    val sxStep = area.width / width
    val syStep = area.height / height
    val sampleSize = (sxStep + syStep) * 0.5f

    var index = 0
    var sy = area.minY
    var y = 0
    while (y < height) {

      var sx = area.minX
      var x = 0
      while (x < width) {
        data(index) = field.apply(sx, sy, sampleSize)

        sx += sxStep
        x += 1
        index += 1
      }

      sy += syStep
      y += 1
    }
  }

  def isInside(x: Int, y: Int): Boolean = x >= 0 && x < width && y >= 0 && y <= height

}