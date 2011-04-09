package org.rasterfun

import functions.{OneFun, Fun}

/**
 * Function that can calculate a raster.
 */
case class RasterFunction(var channels: Map[Symbol, Fun]) {

  def calculateValue(channel: Symbol, x: Float, y: Float, sampleSize: Float): Float = {
    channels.get(channel) match {
      case Some(ch) => ch.apply(x, y, sampleSize)
      case None => 0f
    }
  }

  def set(name: Symbol, function: Fun) {
    channels += (name -> function)
  }


  def calculateBuffer(buffer: Array[Int], width: Int, height: Int, area: Area, includeAlpha: Boolean = true) {
    require(buffer.length == width * height, "Buffer size should match width * heigth")
    require(channels.contains('red))
    require(channels.contains('green))
    require(channels.contains('blue))
    if (includeAlpha) require(channels.contains('alpha))

    def clampScale(v: Float): Int = {
      if (v < 0) 0
      else if (v > 1) 0xff
      else (v * 0xff).toInt
    }

    val red = channels('red)
    val green = channels('green)
    val blue = channels('blue)
    val alpha = if (includeAlpha) channels('alpha) else OneFun

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
        val r = clampScale(red(sx, sy, sampleSize))
        val g = clampScale(green(sx, sy, sampleSize))
        val b = clampScale(blue(sx, sy, sampleSize))
        val a = clampScale(alpha(sx, sy, sampleSize))
        buffer(index) =
                ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8) |
                ((b & 0xFF) << 0)

        sx += sxStep
        x += 1
        index += 1
      }

      sy += syStep
      y += 1
    }
  }
}
